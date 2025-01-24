package com.fwe.flyingwhiteelephant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.enums.ConsentResult;
import com.fwe.flyingwhiteelephant.enums.DeliverStatus;
import com.fwe.flyingwhiteelephant.enums.TransactionStatus;
import com.fwe.flyingwhiteelephant.model.*;
import com.fwe.flyingwhiteelephant.service.crypto.IdentityType;
import com.fwe.flyingwhiteelephant.service.crypto.Wallet;
import com.fwe.flyingwhiteelephant.utils.BlockchainUtils;
import com.fwe.flyingwhiteelephant.utils.HashUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Getter
@Component
@Slf4j
public class BlockchainSupport extends BlockchainContextService {
    private final Wallet wallet;
    private final BlockchainStorageService storageService;
    private final SmartContractSupport smartContractSupport;
    final ObjectMapper mapper = new ObjectMapper();
    private static final ExecutorService consentProcessPool = Executors.newFixedThreadPool(4);
    private static final ExecutorService deliverBlockPool = Executors.newFixedThreadPool(4);


    public BlockchainSupport(Wallet wallet, BlockchainStorageService storageService, SmartContractSupport smartContractSupport) {
        this.wallet = wallet;
        this.storageService = storageService;
        this.smartContractSupport = smartContractSupport;
    }

    public boolean verify(Block block) {
        long latestBlockHeight = storageService.getLatestBlockHeight();
        if (latestBlockHeight > 0) {
            if (block.getHeader().getHeight() != latestBlockHeight + 1) {
                log.error("Block height is not correct, expected: {}, actual: {}", latestBlockHeight + 1, block.getHeader().getHeight());
                // start to get the latest block from the leader node asynchronously, 4 threads avoid the deadlock
                CompletableFuture.runAsync(this::catchup, Executors.newFixedThreadPool(4));
                return false;
            }
        }
        // verify the block signature
        if (!wallet.getIdentity(block.getSigner()).verify(block.getHeader().getHash(), block.getSignature())) {
            log.error("Block signature is not correct, block height: {}, block hash: {}, signature: {}",
                    block.getHeader().getHeight(), block.getHeader().getHash(), block.getSignature());
            return false;
        }
        return true;
    }

    public void writeBlock(Block block) {
        // update the state db by execute the transactions
        for (Transaction transaction : block.getTransactions()) { // execute the transaction
            Map<String, String> callResponse = smartContractSupport.call(transaction.getPayload().getSmartContract(),
                    "1.0", transaction.getPayload().getArgs());
            transaction.getPayload().setWriteSet(
                    callResponse
            );
            transaction.getHeader().setStatus(TransactionStatus.COMMITTED.name());
        }
        log.info("Write block to the blockchain block height: {}, block hash: {}", block.getHeader().getHeight(), block.getHeader().getHash());
        // Write the block to the blockchain and link the parent is missing
        Block perviousBlock = getBlockByHeight(block.getHeader().getHeight() - 1);
        if (perviousBlock != null && !StringUtils.hasLength(block.getHeader().getParentHash())) {
            block.getHeader().setParentHash(perviousBlock.getHeader().getHash());
        }
        storageService.storeBlock(block);
    }

    public Long getLatestHeight() {
        return storageService.getLatestBlockHeight();
    }

    public Block getBlockByHeight(Long height) {
        return storageService.getBlockByHeight(height);
    }

    public List<Block> getBlocks(Long start, Long end) {
        List<CompletableFuture<Block>> futures = LongStream.rangeClosed(start, end)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> getBlockByHeight(i)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public void catchup() {
        if (!this.context.getCurrentRaftServer().isLeader() && this.context.getCurrentRaftServer().getLeaderNodeId() > 0) { //not leader node and raft server is started and leader node is elected
            // get the latest block height from the leader node
            Long currentHeight = getLatestHeight();
            log.info("Current height of the node: {}", currentHeight);
            Optional<NodeClient> leaderNodeClient = Optional.ofNullable(this.context.getNodeClientMap().get(this.context.getCurrentRaftServer().getLeaderNodeId()));
            leaderNodeClient.ifPresent(nodeClient -> {
                long remoteHeight = nodeClient.getLatestHeight().orElse(0L);
                if (currentHeight < remoteHeight) {
                    log.info("Catch up the latest blocks from leader node, current height: {}, remote height: {}", currentHeight, remoteHeight);
                    // get the blocks from the leader node
                    List<Block> blocks = nodeClient.getBlocks(currentHeight + 1, remoteHeight).orElse(Collections.emptyList());
                    if (!blocks.isEmpty()) {
                        // write the blocks to the blockchain
                        log.info("Write the missing blocks to the ledger, total blocks: {}", blocks.size());
                        blocks.forEach(this::writeBlock);
                    }
                }
            });
        }
    }

    public Block proposeBlock(Transaction[] orderedTransactions, AtomicLong currentBlockHeight, String chainId) {
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
                .channelId(chainId)
                .height(currentBlockHeight.get())
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
                Block perviousBlock = getBlockByHeight(currentBlockHeight.get() - 1);
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

    public ConsentResult consent(Block block, List<Node> nodes) {
        int consentStatus = 0;
        // Consent the block
        if (this.verify(block)) {
            consentStatus = 1;
            //deliver the block to other nodes
            List<Optional<DeliverStatus>> deliverStatus = deliverConsentRequest(block, nodes);
            for (Optional<DeliverStatus> status : deliverStatus) {
                consentStatus = consentStatus & status.orElse(DeliverStatus.FAILED).getCode();
            }
        }
        return consentStatus == 1 ? ConsentResult.SUCCESS : ConsentResult.FAIL;
    }

    public List<Optional<DeliverStatus>> deliverConsentRequest(Block block, List<Node> distributedNodes) {
        log.debug("Deliver block consent request to other active nodes");
        // Deliver the block to other nodes
        List<CompletableFuture<Optional<DeliverStatus>>> futures = distributedNodes.stream()
                .map(nodeConfig -> CompletableFuture.supplyAsync(() -> this.context.getNodeClientMap().get(nodeConfig.getId())
                                .deliverConsentRequest(block), consentProcessPool)
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

    public void deliverBlocks(Block block, List<Node> distributedNodes) {
        log.debug("Deliver block to other active nodes");
        // Deliver the block to other nodes
        distributedNodes
                .forEach(nodeConfig -> CompletableFuture.runAsync(() -> this.context.getNodeClientMap().get(nodeConfig.getId()).deliverBlocks(block), deliverBlockPool)
                        .exceptionally(
                                e -> {
                                    log.error("Failed to send block to node: {}", nodeConfig.getId(), e);
                                    return null;
                                }
                        ));
    }
}
