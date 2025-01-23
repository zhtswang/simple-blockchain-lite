package com.fwe.flyingwhiteelephant.service.consent.raft;

import com.fwe.flyingwhiteelephant.model.Node;
import com.fwe.flyingwhiteelephant.service.consent.raft.protocol.ConsentGrpc;
import com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class RaftClient {
    private final URI serverURI;
    private final Node node;
    public RaftClient(Node peerNode) {
        this.node = peerNode;
        this.serverURI = peerNode.getEndpoint().getUri();
    }

    public <R> Optional<R> channelTemplate(Function<ConsentGrpc.ConsentBlockingStub, R> callback) {
        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort() + 1000)
                    .usePlaintext()
                    .build();
            ConsentGrpc.ConsentBlockingStub stub = ConsentGrpc.newBlockingStub(channel).withWaitForReady();
            return Optional.of(callback.apply(stub));
        } catch (Exception e) {
            log.error("Error sending request to peer {}", serverURI);
            // how to return a default value
            return Optional.empty();
        } finally {
            if (channel != null) {
                channel.shutdown();
                try {
                    if (channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        channel.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    channel.shutdownNow();
                }
            }
        }
    }

    public void broadcastHeartbeat(RaftState state) {
        // implement the logic
        channelTemplate((stub) -> {
            Raft.HeartbeatRequest request = Raft.HeartbeatRequest.newBuilder()
                    .setTerm(state.getCurrentTerm())
                    .setLeaderId(state.getLeaderNodeId())
                    .build();
            Raft.HeartbeatResponse response = stub.handleHeartbeat(request);
            return response.getStatus();
        });
    }

    public Optional<Integer> requestVote(RaftState state) {
        Raft.VoteRequest request = Raft.VoteRequest.newBuilder()
                .setTerm(state.getCurrentTerm())
                .setCandidateId(state.getVotedFor())
                .build();
        return channelTemplate(stub -> {
            Raft.VoteResponse voteResp = stub.handleRequestVote(request);
            return voteResp.getStatus();
        });
    }

    public Optional<Integer> sendLogEntries(ConcurrentMap<Long, LogEntry> logEntries, RaftState state) {
        // implement the logic
        List<Raft.LogEntry> entries = logEntries.keySet().stream()
                .map(logEntryKey -> Raft.LogEntry.newBuilder()
                        .setIndex(logEntryKey)
                        .setTerm(logEntries.get(logEntryKey).getTerm())
                        .setCommand(logEntries.get(logEntryKey).getCommand())
                        .build())
                .toList();
        Raft.AppendEntriesRequest request = Raft.AppendEntriesRequest.newBuilder()
                .setTerm(state.getCurrentTerm())
                .setLeaderId(state.getLeaderNodeId())
                .addAllEntries(entries)
                .build();
        return channelTemplate(stub -> {
            Raft.AppendEntriesResponse appendEntriesResponse = stub.handleAppendEntries(request);
            return appendEntriesResponse.getStatus();
        });
    }
}