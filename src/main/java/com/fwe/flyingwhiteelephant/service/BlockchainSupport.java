package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.enums.TransactionStatus;
import com.fwe.flyingwhiteelephant.model.*;
import com.fwe.flyingwhiteelephant.service.crypto.Wallet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Getter
@Component
@Slf4j
public class BlockchainSupport {
    private final Wallet wallet;
    private final BlockchainStorageService storageService;
    private final SmartContractSupport smartContractSupport;
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
        // Write the block to the blockchain
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
}
