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
        // Update current block height before election
        Long currentHeight = context.getBlockchainSupport().getLatestHeight();
        
        // 增加当前任期并投票给自己
        RaftState.updateState(() -> {
            state.setCurrentTerm(state.getCurrentTerm() + 1);
            state.setVoteStatus(VoteStatus.PROGRESS);
            state.setVotedFor(nodeId);
            state.setLastLogHeight(currentHeight);
            log.info("Node {}:{} start election, term {}, voted for node:{}, current role {}, block height {}", 
                domainOrIp, port, state.getCurrentTerm(), state.getVotedFor(),
                state.getRole(), currentHeight);
            state.setRole(Role.CANDIDATE);
        });

        AtomicInteger votes = new AtomicInteger(1);
        // Process vote requests concurrently
        List<CompletableFuture<Void>> voteFutures = peers.stream()
            .map(peer -> CompletableFuture.runAsync(() -> 
                this.context.getRaftClientMap().get(peer.getId())
                    .requestVote(state)
                    .ifPresent(status -> {
                        if (status == 1) {
                            votes.getAndIncrement();
                        }
                    }), transactionForwardPool))
            .toList();

        // Wait for all votes to complete with a timeout
        try {
            CompletableFuture.allOf(voteFutures.toArray(new CompletableFuture[0]))
                .get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.warn("Vote collection interrupted or timed out: {}", e.getMessage());
        }
            
        if (votes.get() > peers.size() / 2) {
            RaftState.updateState(() -> {
                state.setRole(Role.LEADER);
                state.setLeaderNodeId(nodeId);
                state.setVoteStatus(VoteStatus.COMPLETED);
            });
            log.info("Node {}:{} get votes {}, extend half of nodes, and become leader in term {}", domainOrIp, port, votes, state.getCurrentTerm());
            for (Node peer : peers) {
                this.context.getRaftClientMap().get(peer.getId()).broadcastHeartbeat(state);
            }
            if (electionTask != null) {
                log.info("Node {}:{} cancels the election task as it is now the leader in term {}", domainOrIp, port, state.getCurrentTerm());
                electionTask.cancel(false);
            }
        }
    }

    public void updateRaftState(Long blockHeight, String channelId) {
        RaftState.updateState(() -> this.getState().getLog().put(blockHeight, LogEntry.builder()
                .term(this.getState().getCurrentTerm())
                .command(channelId).build()));
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
        // implement the logic
        log.info("Node {}:{} receive heartbeat from leader {} in term {}", domainOrIp, port, request.getLeaderId(), request.getTerm());
        if (state.getRole().equals(Role.LEADER)) {
            // reset and re-election
            RaftState.updateState(() -> {
                state.setVotedFor(-1L);
                state.setLeaderNodeId(-1L); //reset leader, re-election
                state.setRole(Role.FOLLOWER);
                state.setVoteStatus(VoteStatus.PROGRESS);
            });
            // continue to vote
            if (electionTask == null || electionTask.isCancelled()) {
                log.info("Node {}:{} re-election as more than one leader in term {} in the cluster.", domainOrIp, port, state.getCurrentTerm());
                startElection();
            }
        } else {
            if (electionTask != null) {
                log.info("Node {}:{} cancel election task in term {}", domainOrIp, port, state.getCurrentTerm());
                electionTask.cancel(false);
            }
            RaftState.updateState(() -> {
                state.setLeaderNodeId(request.getLeaderId());
                state.setVoteStatus(VoteStatus.COMPLETED);
                state.setRole(Role.FOLLOWER);
            });
        }

        Raft.HeartbeatResponse response = Raft.HeartbeatResponse.newBuilder()
                .setStatus(1).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void handleAppendEntries(Raft.AppendEntriesRequest request, StreamObserver<Raft.AppendEntriesResponse> responseObserver) {
        // implement the logic
        log.info("Node {}:{} receive append entries from leader {} in term {}", domainOrIp, port, request.getLeaderId(), request.getTerm());
        var entries = request.getEntriesList();

        // append the entries to the log
        entries.forEach(entry -> state.getLog().putIfAbsent(entry.getIndex(), LogEntry.builder()
                .term(entry.getTerm())
                .command(entry.getCommand())
                .build()));

        Raft.AppendEntriesResponse response = Raft.AppendEntriesResponse.newBuilder()
                .setStatus(1).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
