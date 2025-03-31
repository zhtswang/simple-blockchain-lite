package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RaftState {
    private final static ReentrantLock stateLock = new ReentrantLock();
    private static final long LOCK_TIMEOUT_MS = 1000;

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
    private ConcurrentMap<Long, LogEntry> logEntries;

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
    @Setter
    @Getter
    private long latestBlockHeight;

    public RaftState() {
        this.role = Role.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1L;
        this.logEntries = new ConcurrentHashMap<>();
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
        boolean locked = false;
        try {
            locked = stateLock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.error("Failed to acquire state lock after {}ms", LOCK_TIMEOUT_MS);
                return;
            }
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("State update interrupted", e);
        } finally {
            if (locked) {
                stateLock.unlock();
            }
        }
    }

    public void addLeaderListener(Runnable leaderListener) {
        this.leaderListener = leaderListener;
    }

    public CompletableFuture<Void> setLeaderNodeId(Long leaderNodeId) {
        Long oldLeaderId = this.leaderNodeId;
        this.leaderNodeId = leaderNodeId;
        // Only trigger listener if:
        // 1. We have a listener
        // 2. New leader is valid (not -1)
        // 3. This is actually a change in leadership
        if (leaderListener != null && leaderNodeId != -1 && !leaderNodeId.equals(oldLeaderId)) {
            return CompletableFuture.runAsync(leaderListener);
        }
        return CompletableFuture.completedFuture(null);
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
            LogEntry entry = logEntries.get(n);
            if (replicationCount > (matchIndex.size() + 1) / 2 && 
                entry != null && entry.getTerm() == currentTerm) {
                commitIndex = n;
            }
        }
    }

    public boolean isLogUpToDate(long lastLogTerm, long lastLogIndex) {
        LogEntry lastEntry = logEntries.get(lastLogHeight);
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
