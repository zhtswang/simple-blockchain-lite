package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "raft")
@Data
@NoArgsConstructor
public class RaftConfig {
    private int electionTimeoutMin = 150;  // milliseconds
    private int electionTimeoutMax = 300;  // milliseconds
    private int heartbeatInterval = 50;    // milliseconds
    private int rpcTimeout = 1000;         // milliseconds
    private int maxBatchSize = 100;        // maximum entries to send in one AppendEntries
    private int snapshotThreshold = 10000; // entries before creating snapshot
    private boolean enablePipelining = true;
    
    public int getRandomElectionTimeout() {
        return electionTimeoutMin + (int)(Math.random() * (electionTimeoutMax - electionTimeoutMin));
    }
} 