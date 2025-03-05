package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.config.NodeConfig;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftClient;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftConfig;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftServer;
import com.fwe.flyingwhiteelephant.service.crypto.Wallet;
import com.fwe.flyingwhiteelephant.service.plugin.PluginServer;
import lombok.Getter;

import java.util.Map;

@Getter
public class BlockchainContext {

    private final BlockchainSupport blockchainSupport;
    private final Wallet wallet;
    private final NodeConfig nodeConfig;
    private final Map<Long, NodeClient> nodeClientMap;
    private final PluginServer pluginServer;
    private final RaftServer currentRaftServer;
    private final NodeServer nodeServer;
    private final Map<Long, RaftClient> raftClientMap;
    private final RaftConfig raftConfig;

    public BlockchainContext(BlockchainSupport blockchainSupport,
                             NodeConfig nodeConfig,
                             Map<Long, NodeClient> nodeClientMap,
                             NodeServer nodeServer,
                             Map<Long, RaftClient> raftClientMap,
                             RaftServer currentRaftServer,
                             PluginServer pluginServer) {
        this.blockchainSupport = blockchainSupport;
        this.wallet = blockchainSupport.getWallet();
        this.nodeConfig = nodeConfig;
        this.nodeClientMap = nodeClientMap;
        this.nodeServer = nodeServer;
        this.raftClientMap = raftClientMap;
        this.currentRaftServer = currentRaftServer;
        this.pluginServer = pluginServer;
        this.raftConfig = new RaftConfig();
        this.nodeServer.setContext(this);
        this.currentRaftServer.setContext(this);
        this.blockchainSupport.setContext(this);
    }
}
