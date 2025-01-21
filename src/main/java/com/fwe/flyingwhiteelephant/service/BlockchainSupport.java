package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.enums.TransactionStatus;
import com.fwe.flyingwhiteelephant.model.*;
import com.fwe.flyingwhiteelephant.service.crypto.Wallet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Getter
@Component
@Slf4j
public class BlockchainSupport {
    private final Wallet wallet;
    private final BlockchainStorageService storageService;
    private final SmartContractSupport smartContractSupport;
    @Setter
    private BlockchainContext context;

    public BlockchainSupport(Wallet wallet, BlockchainStorageService storageService, SmartContractSupport smartContractSupport) {
        this.wallet = wallet;
        this.storageService = storageService;
        this.smartContractSupport = smartContractSupport;
    }

    public boolean verify(Block block) {
        // TODO: Verify the block, need read the latest height from storage
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
}
