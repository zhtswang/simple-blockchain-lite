package com.fwe.flyingwhiteelephant.service.consent.raft;

import com.fwe.flyingwhiteelephant.model.Node;
import com.fwe.flyingwhiteelephant.service.BlockchainContext;
import com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft;
import com.fwe.flyingwhiteelephant.service.consent.raft.protocol.ConsentGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

@Slf4j
public class RaftServer extends ConsentGrpc.ConsentImplBase {
    @Getter
    private final RaftState state;
    private final List<Node> peers;
    private final int port;
    private final Long nodeId;
    private final String domainOrIp;
    private final ScheduledExecutorService executorService;
    private ScheduledFuture<?> monitorElectionTask;
    private Server raftServer;
    private ScheduledFuture<?> electionTask;
    private final ExecutorService transactionForwardPool = Executors.newFixedThreadPool(4);
    @Getter
    @Setter
    private BlockchainContext context;

    public RaftServer(Node currentNode, List<Node> raftNodePeers) {
        this.state = new RaftState();
        // rpc port is 1000 more than the server port
        this.port = currentNode.getEndpoint().getUri().getPort() + 1000;
        this.nodeId = currentNode.getId();
        this.domainOrIp = currentNode.getEndpoint().getUri().getHost();
        // remove self from peers
        executorService = new ScheduledThreadPoolExecutor(4);
        try {
            this.raftServer = ServerBuilder.forPort(port).addService(this).build().start();
            log.info("Raft Server started, listening on {}", port);
        } catch (IOException e) {
            log.error("Error starting Raft server", e);
        }
        this.peers = raftNodePeers.stream().filter(node -> !node.getId().equals(nodeId)).toList();
    }

    public boolean isLeader() {
        return (Objects.equals(state.getLeaderNodeId(), nodeId));
    }

    public Long getLeaderNodeId() {
        return state.getLeaderNodeId();
    }

    public synchronized void addLeaderListener(Runnable listener) {
        state.addLeaderListener(listener);
    }

    public void stop() {
        if (raftServer != null) {
            raftServer.shutdown();
        }
    }

    public CountDownLatch startElection() {
        // implement the logic
        // ThreadLocalRandom.current().nextInt(100, 100 * 2 + 1)
        CountDownLatch latch = new CountDownLatch(1);
        Long currentHeight = context.getBlockchainSupport().getLatestHeight();
        RaftState.updateState(() -> {
            state.setLastLogHeight(currentHeight);
        });
        electionTask = executorService.scheduleAtFixedRate(
                () -> {
                    if (!state.getRole().equals(Role.LEADER) && !state.getVoteStatus().equals(VoteStatus.COMPLETED)) {
                        electLeader();
                    }
                }, nodeId * 100, (100 + nodeId * 50), java.util.concurrent.TimeUnit.MILLISECONDS);
        monitorElectionTask = executorService.scheduleAtFixedRate(
                () -> {
                    if (electionTask.isDone() && state.getLeaderNodeId() > 0) { // leader is elected
                        latch.countDown();
                        // stop the monitor task
                        monitorElectionTask.cancel(false);
                    }
                }, 0, 1, TimeUnit.MILLISECONDS);

        return latch;
    }

    // 开始选举
    private void electLeader() {
        // Reset election timeout with randomization
        int electionTimeout = context.getRaftConfig().getRandomElectionTimeout();
        
        // Increment term and vote for self
        RaftState.updateState(() -> {
            state.setCurrentTerm(state.getCurrentTerm() + 1);
            state.setVoteStatus(VoteStatus.PROGRESS);
            state.setVotedFor(nodeId);
            state.updateLastHeartbeat(); // Reset heartbeat timer
            state.setRole(Role.CANDIDATE);
        });

        // Collect votes concurrently with timeout
        AtomicInteger votes = new AtomicInteger(1); // Vote for self
        CountDownLatch votingComplete = new CountDownLatch(peers.size());
        
        peers.forEach(peer -> CompletableFuture.runAsync(() -> {
            try {
                this.context.getRaftClientMap().get(peer.getId())
                    .requestVote(state)
                    .ifPresent(status -> {
                        if (status == 1) {
                            votes.getAndIncrement();
                        }
                    });
            } finally {
                votingComplete.countDown();
            }
        }, transactionForwardPool));

        try {
            // Wait for all votes or timeout
            boolean votingFinished = votingComplete.await(electionTimeout, TimeUnit.MILLISECONDS);
            if (!votingFinished) {
                log.info("Node {}:{} start election, term {}, voted for node:{}, current role {}, block height {}, timeout {}ms",
                        domainOrIp, port, state.getCurrentTerm(), state.getVotedFor(),
                        state.getRole(), state.getLastLogHeight(), electionTimeout);
                return;
            }
            
            // Check if we won the election
            if (votes.get() > peers.size() / 2) {
                becomeLeader();
            }
        } catch (InterruptedException e) {
            log.error("Election interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private void becomeLeader() {
        RaftState.updateState(() -> {
            state.setRole(Role.LEADER);
            state.setLeaderNodeId(nodeId);
            state.setVoteStatus(VoteStatus.COMPLETED);
            state.resetLeaderState(peers.stream().map(Node::getId).toList());
        });
        
        log.info("Node {}:{} became leader in term {}", domainOrIp, port, state.getCurrentTerm());
        
        // Start heartbeat immediately
        sendHeartbeats();
        
        // Schedule regular heartbeats
        if (electionTask != null) {
            log.info("Cancelling election task as node is now leader");
            electionTask.cancel(false);
        }
        
        // Schedule heartbeat task
        if (context.getRaftConfig().isEnablePipelining()) {
            executorService.scheduleAtFixedRate(
                    this::sendHeartbeats,
                    0,
                    context.getRaftConfig().getHeartbeatInterval(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

    private void sendHeartbeats() {
        if (!isLeader()) {
            return;
        }

        peers.forEach(peer -> CompletableFuture.runAsync(() -> {
            try {
                Long peerId = peer.getId();
                Long nextIdx = state.getNextIndex().get(peerId);

                // Prepare log entries to send
                List<LogEntry> entriesToSend = new ArrayList<>();
                if (nextIdx != null && nextIdx <= state.getLastLogHeight()) {
                    for (long i = nextIdx; i <= state.getLastLogHeight(); i++) {
                        LogEntry entry = state.getLogEntries().get(i);
                        if (entry != null) {
                            entriesToSend.add(entry);
                            if (entriesToSend.size() >= context.getRaftConfig().getMaxBatchSize()) {
                                break;
                            }
                        }
                    }
                }

                // Send heartbeat with any pending entries
                this.context.getRaftClientMap().get(peerId)
                    .broadcastHeartbeat(state, entriesToSend)
                    .ifPresent(status -> {
                        if (status == 1 && !entriesToSend.isEmpty()) {
                            // Update matchIndex and nextIndex on successful replication
                            state.getMatchIndex().put(peerId,
                                entriesToSend.get(entriesToSend.size() - 1).getIndex());
                            state.getNextIndex().put(peerId,
                                state.getMatchIndex().get(peerId) + 1);

                            // Try to advance commit index
                            state.updateCommitIndex();
                        }
                    });
            } catch (Exception e) {
                log.error("Error sending heartbeat to peer: {}", peer.getId(), e);
            }
        }, transactionForwardPool));
    }

    public void updateRaftState(Long blockHeight, String channelId) {
        RaftState.updateState(() -> {
            LogEntry entry = LogEntry.builder()
                .term(this.getState().getCurrentTerm())
                .command(channelId)
                .index(blockHeight)
                .timestamp(System.currentTimeMillis())
                .build();
            
            if (!entry.isValid()) {
                log.error("Invalid log entry: {}", entry);
                return;
            }
            
            this.getState().getLogEntries().put(blockHeight, entry);
            this.getState().setLastLogHeight(Math.max(
                this.getState().getLastLogHeight(),
                blockHeight
            ));
        });
    }

    public void sendLogEntries(ConcurrentMap<Long, LogEntry> logEntries, List<Node> distributeNodes) {
        // send the log entries to other nodes
        log.info("Send log entries to other nodes");
        var sendLogFutures = distributeNodes.stream()
                .map(nodeConfig -> CompletableFuture.supplyAsync(() -> this.context
                                .getRaftClientMap().get(nodeConfig.getId())
                                .sendLogEntries(logEntries, this.context
                                        .getCurrentRaftServer().getState()), transactionForwardPool)
                        .exceptionally(
                                e -> {
                                    log.error("Failed to send log entries to node: {}", nodeConfig.getId(), e);
                                    return Optional.empty();
                                }
                        )).toList();
        var resp = CompletableFuture
                .allOf(sendLogFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> sendLogFutures.stream().map(CompletableFuture::join).toList())
                .join();
        log.info("Send log entries to other nodes response: {}", resp);
        for (Optional<Integer> status : resp) {
            status.ifPresent(s -> {
                if (s != 1) {
                    log.error("Failed to send log entries to other nodes");
                }
            });
        }
    }

    @Override
    public void handleRequestVote(Raft.VoteRequest request, StreamObserver<Raft.VoteResponse> responseObserver) {
        int status = 0; // 0: refuse, 1: agree
        
        // Only vote if the candidate's log is at least as up-to-date as ours
        boolean isLogUpToDate = request.getLastLogHeight() >= state.getLastLogHeight();
        
        if (request.getTerm() > state.getCurrentTerm() && isLogUpToDate) {
            log.info("Node {}:{} update term to {}, vote for node:{} in term {}, candidate log height: {}, our log height: {}", 
                domainOrIp, port, request.getTerm(), request.getCandidateId(), state.getCurrentTerm(), 
                request.getLastLogHeight(), state.getLastLogHeight());
            RaftState.updateState(() -> {
                state.setCurrentTerm(request.getTerm());
                state.setRole(Role.FOLLOWER);
                state.setVotedFor(request.getCandidateId());
            });
            status = 1;
        } else if (request.getTerm() == state.getCurrentTerm() 
                && state.getVotedFor() == -1L 
                && isLogUpToDate) {
            RaftState.updateState(() -> state.setVotedFor(request.getCandidateId()));
            status = 1;
        }
        
        log.debug("Node {}:{} vote for node:{} in term {}, vote result: {}, candidate log height: {}, our log height: {}", 
            domainOrIp, port, request.getCandidateId(), request.getTerm(), status,
            request.getLastLogHeight(), state.getLastLogHeight());
        
        Raft.VoteResponse response = Raft.VoteResponse.newBuilder()
                .setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void handleHeartbeat(Raft.HeartbeatRequest request, StreamObserver<Raft.HeartbeatResponse> responseObserver) {
        log.info("Node {}:{} receive heartbeat from leader {} in term {}, height: {}",
            domainOrIp, port, request.getLeaderId(), request.getTerm(), state.getLastLogHeight());
        
        if (request.getTerm() > state.getCurrentTerm()) {
            stepDown(request.getTerm());
        } else if (request.getTerm() < state.getCurrentTerm()) {
            // Reject heartbeat from old term
            responseObserver.onNext(Raft.HeartbeatResponse.newBuilder().setStatus(0).build());
            responseObserver.onCompleted();
            return;
        }
        
        if (state.getRole().equals(Role.LEADER) && request.getLeaderId() != nodeId) {
            // Another leader exists in the same term
            stepDown(request.getTerm());
            startElection();
        } else {
            state.updateLastHeartbeat();
            RaftState.updateState(() -> {
                state.setLeaderNodeId(request.getLeaderId());
                state.setRole(Role.FOLLOWER);
                state.setVoteStatus(VoteStatus.COMPLETED);
            });
        }
        
        responseObserver.onNext(Raft.HeartbeatResponse.newBuilder().setStatus(1).build());
        responseObserver.onCompleted();
    }

    @Override
    public void handleAppendEntries(Raft.AppendEntriesRequest request, 
                                  StreamObserver<Raft.AppendEntriesResponse> responseObserver) {
        int status = 0;
        try {
            // Update term if needed
            if (request.getTerm() > state.getCurrentTerm()) {
                stepDown(request.getTerm());
            }
            
            // Validate request
            if (request.getTerm() < state.getCurrentTerm()) {
                log.warn("Rejected AppendEntries: term {} < current term {}", 
                    request.getTerm(), state.getCurrentTerm());
            } else {
                // Valid AppendEntries, update leader and heartbeat
                state.setLeaderNodeId(request.getLeaderId());
                state.updateLastHeartbeat();
                
                // Process entries
                var entries = request.getEntriesList();
                if (!entries.isEmpty()) {
                    for (Raft.LogEntry entry : entries) {
                        state.getLogEntries().putIfAbsent(entry.getIndex(),
                            LogEntry.builder()
                                .index(entry.getIndex())
                                .term(entry.getTerm())
                                .command(entry.getCommand())
                                .timestamp(System.currentTimeMillis())
                                .build());
                    }
                    
                    // Update lastLogHeight
                    state.setLastLogHeight(Math.max(
                        state.getLastLogHeight(),
                        entries.get(entries.size() - 1).getIndex()
                    ));
                }
                status = 1;
            }
        } catch (Exception e) {
            log.error("Error handling AppendEntries", e);
        }
        
        responseObserver.onNext(Raft.AppendEntriesResponse.newBuilder()
            .setStatus(status)
            .build());
        responseObserver.onCompleted();
    }

    private void stepDown(int newTerm) {
        RaftState.updateState(() -> {
            state.setCurrentTerm(newTerm);
            state.setRole(Role.FOLLOWER);
            state.setVotedFor(-1L);
            if (electionTask != null) {
                electionTask.cancel(false);
            }
        });
    }
}
