package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.config.NodeConfig;
import com.fwe.flyingwhiteelephant.enums.ConsentResult;
import com.fwe.flyingwhiteelephant.model.*;

import java.util.*;
import java.util.concurrent.*;

import com.fwe.flyingwhiteelephant.service.consent.raft.RaftClient;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftServer;
import com.fwe.flyingwhiteelephant.service.crypto.Identity;
import com.fwe.flyingwhiteelephant.service.plugin.PluginServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class Blockchain {

    @Value("${blockchain.chain_id:FlyingWhiteElephant}")
    private String chainId;

    @Value("${node.id:1}")
    private Long nodeId;

    private final AtomicLong currentBlockHeight = new AtomicLong(0L);

    private static final ExecutorService blockProposalPool = Executors.newFixedThreadPool(8);
    private static final ExecutorService transactionForwardPool = Executors.newFixedThreadPool(8);

    private static final CachedBlockPriorityQueue blockCacheQueue = new CachedBlockPriorityQueue();

    private final BlockchainContext blockchainContext;

    public Blockchain(BlockchainSupport blockchainSupport,
                      NodeConfig nodeConfig,
                      @Qualifier("nodeClientMap") Map<Long, NodeClient> nodeClientMap,
                      @Qualifier("nodeServer") NodeServer nodeServer,
                      @Qualifier("raftClientMap") Map<Long, RaftClient> raftClientMap,
                      @Qualifier("raftServer") RaftServer currentRaftServer,
                      @Qualifier("pluginServer") PluginServer pluginServer) {

        this.blockchainContext = new BlockchainContext(
                blockchainSupport,
                nodeConfig,
                nodeClientMap,
                nodeServer,
                raftClientMap,
                currentRaftServer,
                pluginServer
        );
        this.blockchainContext.getNodeServer()
                .getTransactionPool()
                .addQueueEventListener(this::transactionPoolListener);
        this.blockchainContext.getCurrentRaftServer().addLeaderListener(blockchainSupport::catchup);
        log.info("Blockchain service initialized");
    }

    public void start() {
        //start node server and make sure the rpc server is workable, then start the raft server
        startNodeServer();
        // start the raft server
        startRaftElection();
        // plugin server start load plugins
        startPluginServer();
        // start the block consumer
        startBlockConsumer();
        // read the current block height
        long latestBlockHeight = getLatestBlockHeight();
        if (latestBlockHeight > 0) {
            // set the current height to the latest block height
            currentBlockHeight.set(latestBlockHeight);
            log.info("Start blockchain, current block height at: {}", latestBlockHeight);
        } else {
            // write the genesis block to the blockchain
            if (isLeaderNode()) {
                log.info("Creating genesis block by the leader node");
                // create a genesis block
                Block genesisBlock = this.blockchainContext.getBlockchainSupport().proposeBlock(null,
                        this.currentBlockHeight,
                        this.chainId);
                this.blockchainContext.getBlockchainSupport().writeBlock(genesisBlock);
                this.blockchainContext.getBlockchainSupport().deliverBlocks(genesisBlock, getDistributeNodes());
            }
        }
    }

    private void startNodeServer() {
        // start the node server
        this.blockchainContext.getNodeServer().start();
    }

    public void startPluginServer() {
        this.blockchainContext.getPluginServer().loadSystemPlugins();
    }

    private void startRaftElection() {
        CountDownLatch latch = this.blockchainContext.getCurrentRaftServer().startElection();
        boolean leaderElectionCompleted;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            leaderElectionCompleted = latch.await(30000, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopWatch.stop();
        }
        if (leaderElectionCompleted) {
            log.info("Leader election completed, leader node id: {}, duration: {}", this.blockchainContext.getCurrentRaftServer().getLeaderNodeId(), stopWatch.getTotalTimeMillis());
        } else {
            log.error("Leader election does not complete, timeout: {}", 30000);
        }
    }

    private void startBlockConsumer() {
        // start the block consumer
        blockCacheQueue.addListener(this::cachedBlocksConsumer);
    }

    // current node is the leader node
    private boolean isLeaderNode() {
        // implement the logic
        return this.blockchainContext.getCurrentRaftServer().isLeader();
    }

    public List<Block> getBlocks(long from, long to) {
        return this.blockchainContext.getBlockchainSupport().getBlocks(from, to);
    }

    public long getLatestBlockHeight() {
        // Get the latest block from the blockchain
        return this.blockchainContext.getBlockchainSupport().getLatestHeight();
    }

    public List<Transaction> orderTransactions(Transaction[] transactions) {
        // Order the transactions
        return Arrays.stream(transactions)
                .sorted(comparing(o -> o.getHeader().getTimestamp()))
                .toList();
    }

    public Map<String, String> broadcast(Transaction... txs) {
        // collect transactions from different client apps and order them
        //TODO: next will validate the payload, it should contain
        return blockchainContext.getNodeServer().handleBroadcastTransactions(txs);
    }

    public Map<String, Map<String, String>> enroll(String username) {
        // enroll the user
        Identity identity = blockchainContext.getWallet().newClientIdentity();
        // onchain the identity by constructing the transaction and forward to the leader node
        Transaction tx = new Transaction();
        tx.setPayload(TransactionPayload.builder()
                        .smartContract("did")
                        .args(List.of("create", "DIDDocument", identity.toJson()))
                .build());
        var result = broadcast(tx);
        return Map.of(username, blockchainContext.getWallet().newClientIdentity().toMap(), "onchainResponse", result);
    }

    private void transactionPoolListener(TransactionPool transactionPool) {
        //order the transactions
        // forward transactions to leader node batch
        Transaction[] txs = transactionPool.getTransactions();
        if (txs.length == 0) {
            // skip the empty blocks generation
            return;
        }
        if (isLeaderNode()) {
            List<Transaction> orderedTransactions = orderTransactions(txs);
            // cut the transactions
            cutTransactions(orderedTransactions);
            log.debug("Clear the emphasized queue");
        } else {
            log.info("Forward transactions to leader node: {}, total transactions: {}", this.blockchainContext.getCurrentRaftServer().getLeaderNodeId(), txs.length);
            Optional<NodeClient> leaderNodeClient = Optional.ofNullable(this.blockchainContext.getNodeClientMap().get(this.blockchainContext.getCurrentRaftServer().getLeaderNodeId()));
            leaderNodeClient.ifPresent(nodeClient -> CompletableFuture.runAsync(() -> {
                var resp = nodeClient.sendTransactions(txs);
                log.info("Forward transactions to leader node: {}, response: {}", this.blockchainContext.getCurrentRaftServer().getLeaderNodeId(), resp.orElse("Failed"));
            }, transactionForwardPool).exceptionally(
                    e -> {
                        log.error("Failed to send transactions to leader node", e);
                        return null;
                    }
            ));
        }
        // clear the queue
        transactionPool.clear();
    }

    public void cutTransactions(List<Transaction> orderedTransactions) {
        blockProposalPool.submit(() -> blockProducer(orderedTransactions));
    }

    private void blockProducer(List<Transaction> orderedTransactions) {
        // start the block producer
        long newBlockHeight = this.currentBlockHeight.incrementAndGet();
        // Cut the transactions
        log.info("Cutting transactions, total transactions: {}, propose block height {}", orderedTransactions.size(), newBlockHeight);
        Block block = this.blockchainContext.getBlockchainSupport().proposeBlock(
                orderedTransactions.toArray(new Transaction[0]),
                this.currentBlockHeight,
                this.chainId);
        // cache the block
        log.info("Put the block: {} to the cache queue.", block.getHeader().getHeight());
        blockCacheQueue.put(block);
    }

    private void cachedBlocksConsumer(Block block) {
        var consentResult = this.blockchainContext.getBlockchainSupport().consent(block, getDistributeNodes());
        if (consentResult.equals(ConsentResult.SUCCESS)) {
            try {
                this.blockchainContext.getBlockchainSupport().writeBlock(block);
                // leader write the block to the local node successfully, then sync the log to other nodes
                this.blockchainContext.getCurrentRaftServer().updateRaftState(block.getHeader().getHeight(), this.chainId);
                // send the leader logs to other nodes
                this.blockchainContext.getCurrentRaftServer().sendLogEntries(this.blockchainContext.getCurrentRaftServer().getState().getLogEntries(), getDistributeNodes());
            } catch (Exception e) {
                log.error("Write Block at local node failed, rollback the block height", e);
                // reset the cache height
                this.currentBlockHeight.decrementAndGet();
            }
            // sync the block to all other nodes
            this.blockchainContext.getBlockchainSupport().deliverBlocks(block, getDistributeNodes());
            // remove the block from the cache
            blockCacheQueue.poll();
        }
    }

    @SneakyThrows
    private List<Node> getDistributeNodes() {
        // exclude node itself
        return this.blockchainContext.getNodeConfig().getNodes().stream().filter(node -> !node.getId().equals(this.nodeId)).toList();
    }
}
