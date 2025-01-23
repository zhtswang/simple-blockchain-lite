package com.fwe.flyingwhiteelephant.config;

import com.fwe.flyingwhiteelephant.model.Node;
import com.fwe.flyingwhiteelephant.service.NodeClient;
import com.fwe.flyingwhiteelephant.service.NodeServer;
import com.fwe.flyingwhiteelephant.service.TransactionPool;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftClient;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftServer;
import com.fwe.flyingwhiteelephant.service.plugin.PluginClient;
import com.fwe.flyingwhiteelephant.service.plugin.PluginServer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BlockchainConfig {
    @Value("${node.id:1}")
    private Long nodeId;
    @Value("${blockchain.block_size: 10}")
    private int CAPACITY = 10;
    @Value("${blockchain.block timeout: 500}")
    private int THRESHOLD = 500;

    @Resource
    NodeConfig nodeConfig;

    @Bean(name = "nodeClientMap")
    public Map<Long, NodeClient> nodeClientMap() {
        Map<Long, NodeClient> nodeClientMap = new HashMap<>();
        nodeConfig.getNodes().forEach(node -> {
            NodeClient nodeClient = new NodeClient(node);
            nodeClientMap.put(node.getId(), nodeClient);
        });
        return nodeClientMap;
    }

    @Bean
    public Node currentNodeConfig() {
        return nodeConfig.getNodes().stream().filter(node -> node.getId().equals(nodeId)).findFirst().orElseThrow();
    }

    @Bean(name = "raftClientMap")
    public Map<Long, RaftClient> raftClientMap() {
        Map<Long, RaftClient> raftClientMap = new HashMap<>();
        nodeConfig.getNodes().forEach(node -> raftClientMap.put(node.getId(), new RaftClient(node)));
        return raftClientMap;
    }

    @Bean(name = "raftServer")
    public RaftServer raftServer() {
        Node node = currentNodeConfig();
        return new RaftServer(node, nodeConfig.getNodes());
    }

    @Bean(name = "nodeServer")
    public NodeServer nodeServer() {
        Node node = currentNodeConfig();
        return new NodeServer(node, new TransactionPool(CAPACITY, THRESHOLD));
    }

    @Bean(name="pluginClient")
    public PluginClient pluginClient() {
        Node node = currentNodeConfig();
        URI uri = node.getEndpoint().getUri();
        int port  = uri.getPort() + 2000;
        if (node.getTls()) {
            return new PluginClient(uri.getHost(), port, node.getPluginClientCertChainPath(), node.getPluginClientPrivateKeyPath());
        }
        return new PluginClient(uri.getHost(), port);
    }

    @Bean(name="pluginServer")
    public PluginServer pluginServer() {
        Node node = currentNodeConfig();
        URI uri = node.getEndpoint().getUri();
        int port = uri.getPort() + 2000;
        if (node.getTls()) {
            if (!StringUtils.hasLength(node.getPluginServerCertChainPath()) || !StringUtils.hasLength(node.getPluginServerPrivateKeyPath())) {
                throw new RuntimeException("TLS is enabled but certChainPath and privateKeyPath are not provided");
            }
            return new PluginServer(port, node.getPluginServerCertChainPath(), node.getPluginServerPrivateKeyPath());
        }
        return new PluginServer(port);
    }
}
