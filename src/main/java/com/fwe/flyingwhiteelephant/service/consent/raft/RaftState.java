package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class RaftState {
    private final static  ReentrantLock stateLock = new ReentrantLock();
    private Runnable leaderListener;
    @Getter
    @Setter
    private Role role;
    @Getter
    @Setter
    private int currentTerm;

    @Getter
    @Setter
    private Long votedFor;

    @Getter
    @Setter
    private VoteStatus voteStatus;

    @Getter
    @Setter
    private ConcurrentMap<Long, LogEntry> log;

    @Getter
    @Setter
    private Long lastLogHeight;

    @Getter
    @Setter
    private Long lastAppliedIndex;

    @Getter
    @Setter
    private Long commitIndex;

    @Getter
    private Long leaderNodeId;

    @Getter
    @Setter
    private Long lastHeartbeat;

    @Getter
    @Setter
    private ConcurrentMap<Long, Long> nextIndex;

    @Getter
    @Setter
    private ConcurrentMap<Long, Long> matchIndex;

    public RaftState() {
        this.role = Role.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1L;
        this.log = new ConcurrentHashMap<>();
        this.voteStatus = VoteStatus.INIT;
        this.leaderNodeId = -1L; // no leader
        this.lastLogHeight = 0L; // initialize with 0
        this.lastAppliedIndex = 0L;
        this.commitIndex = 0L;
        this.lastHeartbeat = System.currentTimeMillis();
        this.nextIndex = new ConcurrentHashMap<>();
        this.matchIndex = new ConcurrentHashMap<>();
    }

    public static void updateState(Runnable runnable) {
        stateLock.lock();
        try {
            runnable.run();
        } finally {
            stateLock.unlock();
        }
    }

    public void addLeaderListener(Runnable leaderListener) {
        this.leaderListener = leaderListener;
    }

    public void setLeaderNodeId(Long leaderNodeId) {
        this.leaderNodeId = leaderNodeId;
        if (leaderListener != null && leaderNodeId != -1) { // leader is elected and notify the listener
            CompletableFuture.runAsync(leaderListener);
        }
    }

    public void resetLeaderState(List<Long> peerIds) {
        nextIndex.clear();
        matchIndex.clear();
        peerIds.forEach(peerId -> {
            nextIndex.put(peerId, lastLogHeight + 1);
            matchIndex.put(peerId, 0L);
        });
    }

    public void updateCommitIndex() {
        if (role != Role.LEADER) {
            return;
        }
        
        // Find the highest matchIndex that is replicated to majority
        for (long n = commitIndex + 1; n <= lastLogHeight; n++) {
            int replicationCount = 1; // Count self
            for (Long matchIdx : matchIndex.values()) {
                if (matchIdx >= n) {
                    replicationCount++;
                }
            }
            
            // Check if we have majority and entry is from current term
            LogEntry entry = log.get(n);
            if (replicationCount > (matchIndex.size() + 1) / 2 && 
                entry != null && entry.getTerm() == currentTerm) {
                commitIndex = n;
            }
        }
    }

    public boolean isLogUpToDate(long lastLogTerm, long lastLogIndex) {
        LogEntry lastEntry = log.get(lastLogHeight);
        if (lastEntry == null) {
            return true;
        }
        
        if (lastLogTerm != lastEntry.getTerm()) {
            return lastLogTerm > lastEntry.getTerm();
        }
        return lastLogIndex >= lastLogHeight;
    }

    public void updateLastHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public boolean hasHeartbeatTimedOut(long timeoutMs) {
        return System.currentTimeMillis() - lastHeartbeat > timeoutMs;
    }
}
