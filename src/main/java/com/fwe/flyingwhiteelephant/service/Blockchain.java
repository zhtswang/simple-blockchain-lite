package com.fwe.flyingwhiteelephant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.config.NodeConfig;
import com.fwe.flyingwhiteelephant.enums.ConsentResult;
import com.fwe.flyingwhiteelephant.enums.DeliverStatus;
import com.fwe.flyingwhiteelephant.model.*;

import java.util.*;
import java.util.concurrent.*;

import com.fwe.flyingwhiteelephant.service.consent.raft.LogEntry;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftClient;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftServer;
import com.fwe.flyingwhiteelephant.service.consent.raft.RaftState;
import com.fwe.flyingwhiteelephant.service.crypto.IdentityType;
import com.fwe.flyingwhiteelephant.service.crypto.Wallet;
import com.fwe.flyingwhiteelephant.service.plugin.PluginServer;
import com.fwe.flyingwhiteelephant.utils.BlockchainUtils;
import com.fwe.flyingwhiteelephant.utils.HashUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private static final ExecutorService blockProcessExeService;

    static {
        blockProcessExeService = Executors.newFixedThreadPool(20);
    }

    private final Wallet wallet;
    final ObjectMapper mapper = new ObjectMapper();
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
        this.wallet = this.blockchainContext.getBlockchainSupport().getWallet();
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
                Block genesisBlock = proposeBlock(null);
                this.blockchainContext.getBlockchainSupport().writeBlock(genesisBlock);
                deliverBlocks(genesisBlock);
            }
        }
    }

    private void startNodeServer() {
        // start the node server
        this.blockchainContext.getNodeServer().start();
    }

    public void startPluginServer() {
        this.blockchainContext.getPluginServer().loadPlugins("default");
    }

    private void startRaftElection() {
        CountDownLatch latch = this.blockchainContext.getCurrentRaftServer().startElection();
        boolean leaderElectionCompleted;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            leaderElectionCompleted = latch.await(10000, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopWatch.stop();
        }
        if (leaderElectionCompleted) {
            log.info("Leader election completed, leader node id: {}, duration: {}", this.blockchainContext.getCurrentRaftServer().getLeaderNodeId(), stopWatch.getTotalTimeMillis());
        } else {
            log.error("Leader election failed, timeout: {}", 10000);
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

    public Block getBlockByHeight(long height) {
        // Get the block by height
        return this.blockchainContext.getBlockchainSupport().getBlockByHeight(height);
    }

    public List<Transaction> orderTransactions(Transaction[] transactions) {
        // Order the transactions
        return Arrays.stream(transactions)
                .sorted(comparing(o->o.getHeader().getTimestamp()))
                .toList();
    }

    public void broadcast(Transaction... txs) {
        // collect transactions from different client apps and order them
        //TODO: next will validate the payload, it should contain
        blockchainContext.getNodeServer().handleBroadcastTransactions(txs);
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
            leaderNodeClient.ifPresent(nodeClient -> CompletableFuture.runAsync(() -> nodeClient.sendTransactions(txs), blockProcessExeService).exceptionally(
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
        blockProcessExeService.submit(() -> blockProducer(orderedTransactions));
    }

    private void blockProducer(List<Transaction> orderedTransactions) {
        // start the block producer
        long newBlockHeight = this.currentBlockHeight.incrementAndGet();
        // Cut the transactions
        log.info("Cutting transactions, total transactions: {}, propose block height {}", orderedTransactions.size(), newBlockHeight);
        Block block = proposeBlock(orderedTransactions.toArray(new Transaction[0]));
        // cache the block
        log.info("Put the block: {} to the cache queue.", block.getHeader().getHeight());
        blockCacheQueue.put(block);
    }

    private void cachedBlocksConsumer(Block block) {
        if (consent(block).equals(ConsentResult.SUCCESS)) {
            try {
                this.blockchainContext.getBlockchainSupport().writeBlock(block);
                // leader write the block to the local node successfully, then sync the log to other nodes
                RaftState.updateState(() -> this.blockchainContext.getCurrentRaftServer().getState().getLog().put(block.getHeader().getHeight(), LogEntry.builder()
                        .term(this.blockchainContext.getCurrentRaftServer().getState().getCurrentTerm())
                        .command(block.getHeader().getChannelId()).build()));
                // send the leader logs to other nodes
                sendLogEntries(this.blockchainContext.getCurrentRaftServer().getState().getLog());
            } catch (Exception e) {
                log.error("Write Block at local node failed, rollback the block height", e);
                // reset the cache height
                this.currentBlockHeight.decrementAndGet();
            }
            // sync the block to all other nodes
            deliverBlocks(block);
            // remove the block from the cache
            blockCacheQueue.poll();
        }
    }

    private void sendLogEntries(ConcurrentMap<Long, LogEntry> logEntries) {
        // send the log entries to other nodes
        log.info("Send log entries to other nodes");
        var sendLogFutures = getNodeConfigWithoutLocal().stream()
                .map(nodeConfig -> CompletableFuture.supplyAsync(() -> this.blockchainContext
                                .getRaftClientMap().get(nodeConfig.getId())
                                .sendLogEntries(logEntries, this.blockchainContext
                                        .getCurrentRaftServer().getState()), blockProcessExeService)
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

    public Block proposeBlock(Transaction[] orderedTransactions) {
        String transactionRoot = null;
        // construct one market root tree
        if (orderedTransactions != null) {
            transactionRoot = BlockchainUtils.generateMerkleRoot(Arrays.stream(orderedTransactions).map(tx -> {
                try {
                    return HashUtils.sha256(mapper.writeValueAsString(tx));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).toList());
        }
        BlockHeader header = BlockHeader.builder()
                .channelId(this.chainId)
                .height(this.currentBlockHeight.get())
                .transactionsRoot(transactionRoot)
                .timestamp(System.currentTimeMillis())
                .build();
        Block block = Block.builder()
                .header(header)
                .transactions(orderedTransactions != null ? orderedTransactions : new Transaction[0])
                .build();
        String blockHash;
        try {
            byte[] hash = MessageDigest.getInstance("SHA256").digest(mapper.writeValueAsBytes(block));
            blockHash = HexFormat.of().formatHex(hash);
            block.getHeader().setHash(blockHash);
            if (orderedTransactions != null) {
                Block perviousBlock = getBlockByHeight(this.currentBlockHeight.get() - 1);
                if (perviousBlock != null) {
                    block.getHeader().setParentHash(perviousBlock.getHeader().getHash());
                }
            }
            // sign the block by the leader
            block.setSignature(wallet.getIdentity(IdentityType.NODE).sign(blockHash));
            block.setSigner(wallet.getIdentity(IdentityType.NODE).toString());
            log.debug("Block signed by {}, hash: {}, signature: {}", wallet.getIdentity(IdentityType.NODE).toString(), blockHash, block.getSignature());
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Propose a new block, block height: {}, block hash: {}", block.getHeader().getHeight(), blockHash);
        return block;
    }

    public ConsentResult consent(Block block) {
        int consentStatus = 0;
        // Consent the block
        if (this.blockchainContext.getBlockchainSupport().verify(block)) {
            consentStatus = 1;
            //deliver the block to other nodes
            List<Optional<DeliverStatus>> deliverStatus = deliverConsentRequest(block);
            for (Optional<DeliverStatus> status : deliverStatus) {
                consentStatus = consentStatus & status.orElse(DeliverStatus.FAILED).getCode();
            }
        }
        return consentStatus == 1 ? ConsentResult.SUCCESS : ConsentResult.FAIL;
    }

    public List<Optional<DeliverStatus>> deliverConsentRequest(Block block) {
        log.debug("Deliver block consent request to other active nodes");
        // Deliver the block to other nodes
        List<CompletableFuture<Optional<DeliverStatus>>> futures = getNodeConfigWithoutLocal().stream()
                .map(nodeConfig -> CompletableFuture.supplyAsync(() -> this.blockchainContext.getNodeClientMap().get(nodeConfig.getId())
                                .deliverConsentRequest(block), blockProcessExeService)
                        .exceptionally(
                                e -> {
                                    log.error("Failed to send block consent request to node: {}", nodeConfig.getId(), e);
                                    return Optional.of(DeliverStatus.FAILED);
                                }
                        )).toList();
        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
                .join();
    }

    public void deliverBlocks(Block block) {
        log.debug("Deliver block to other active nodes");
        // Deliver the block to other nodes
        getNodeConfigWithoutLocal()
                .forEach(nodeConfig -> CompletableFuture.runAsync(() -> this.blockchainContext.getNodeClientMap().get(nodeConfig.getId()).deliverBlocks(block), blockProcessExeService)
                        .exceptionally(
                                e -> {
                                    log.error("Failed to send block to node: {}", nodeConfig.getId(), e);
                                    return null;
                                }
                        ));
    }

    @SneakyThrows
    private List<Node> getNodeConfigWithoutLocal() {
        // exclude node itself
        return this.blockchainContext.getNodeConfig().getNodes().stream().filter(node -> !node.getId().equals(this.nodeId)).toList();
    }
}
