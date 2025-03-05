package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

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
    private Long leaderNodeId;

    @Getter
    @Setter
    private Long lastLogHeight;

    public RaftState() {
        this.role = Role.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1L;
        this.log = new ConcurrentHashMap<>();
        this.voteStatus = VoteStatus.INIT;
        this.leaderNodeId = -1L; // no leader
        this.lastLogHeight = 0L; // initialize with 0
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

}
