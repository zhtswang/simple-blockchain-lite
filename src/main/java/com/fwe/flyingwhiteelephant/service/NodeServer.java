package com.fwe.flyingwhiteelephant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.enums.DeliverStatus;
import com.fwe.flyingwhiteelephant.enums.TransactionStatus;
import com.fwe.flyingwhiteelephant.enums.TransactionType;
import com.fwe.flyingwhiteelephant.model.*;
import com.fwe.flyingwhiteelephant.service.crypto.IdentityType;
import com.fwe.flyingwhiteelephant.service.rpc.node.BlockServiceGrpc;
import com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC;
import com.fwe.flyingwhiteelephant.utils.BlockchainUtils;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

@Slf4j
public class NodeServer extends BlockServiceGrpc.BlockServiceImplBase {
    static final ObjectMapper mapper = new ObjectMapper();
    private final NodeEndpoint nodeEndpoint;
    @Getter
    private final TransactionPool transactionPool;
    @Getter
    @Setter
    private BlockchainContext context;
    private final Node nodeConfig;
    public NodeServer(Node nodeConfig, TransactionPool transactionPool) {
        this.nodeConfig = nodeConfig;
        this.nodeEndpoint = nodeConfig.getEndpoint();
        this.transactionPool = transactionPool; // one node one transaction pool to catch the transaction from other nodes
    }

    public void start(){
        log.info("Start one GRPC server for node service in port: {}", nodeEndpoint.getUri().getPort() + 3000);
        try {
            NettyServerBuilder.forPort(nodeEndpoint.getUri().getPort() + 3000)
                    .addService(this)
                    .build()
                    .start();
        } catch (IOException e) {
           log.error("Failed to start the GRPC server for node service", e);
        }
    }

    @Override
    public void handleGetBlockStream(NodeRPC.BlockRequest request, StreamObserver<NodeRPC.BlockResponse> responseObserver) {
        log.info("Get all blocks from {}-{}", request.getStartBlockNumber(), request.getEndBlockNumber());
        List<CompletableFuture<Block>> futures = LongStream.rangeClosed(request.getStartBlockNumber(), request.getEndBlockNumber())
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> this.context.getBlockchainSupport().getStorageService().getBlockByHeight(i)))
                .toList();

        List<Block> blocksFromDB =  futures.stream()
                .map(CompletableFuture::join)
                .toList();
        // convert the block to grpc block
        List<NodeRPC.Block> blocks = blocksFromDB.stream()
                .map(NodeServiceUtils::convertBlock)
                .toList();
        responseObserver.onNext(NodeRPC.BlockResponse.newBuilder().addAllBlocks(blocks).build());
        responseObserver.onCompleted();
    }

    @Override
    public void handleGetLatestBlockHeight(NodeRPC.BlockHeightRequest request, StreamObserver<NodeRPC.BlockHeight> responseObserver) {
        log.info("Handle latest block height requests node server:{}", request.getNodeId());
        long latestBlockHeight = this.context.getBlockchainSupport().getStorageService().getLatestBlockHeight();
        responseObserver.onNext(NodeRPC.BlockHeight.newBuilder().setHeight(latestBlockHeight).build());
        responseObserver.onCompleted();
    }

    @Override
    public void handleConsentRequest(NodeRPC.Block request, StreamObserver<NodeRPC.DeliverStatus> responseObserver) {
        // verify the block
        Block block = NodeServiceUtils.convertGRPCBlock(List.of(request)).get(0);
        if (this.context.getBlockchainSupport().verify(block)) {
            responseObserver.onNext(NodeRPC.DeliverStatus.newBuilder().setCode(DeliverStatus.DELIVERED.getCode())
                    .setDescription(DeliverStatus.DELIVERED.getDescription())
                    .build());
        } else {
            responseObserver.onNext(NodeRPC.DeliverStatus.newBuilder().setCode(DeliverStatus.FAILED.getCode())
                    .setDescription(DeliverStatus.FAILED.getDescription())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void handleDeliverBlock(NodeRPC.Block request, StreamObserver<NodeRPC.DeliverStatus> responseObserver) {
        // verify and store the received block
        Block block = NodeServiceUtils.convertGRPCBlock(List.of(request)).get(0);
        if (!this.context.getBlockchainSupport().verify(block)) {
            log.error("Failed to verify the block:{}", block.getHeader().getHeight());
            responseObserver.onNext(NodeRPC.DeliverStatus.newBuilder().setCode(DeliverStatus.FAILED.getCode())
                    .setDescription(DeliverStatus.FAILED.getDescription())
                    .build());
            responseObserver.onCompleted();
            return;
        }
        this.context.getBlockchainSupport().writeBlock(block);
        responseObserver.onNext(NodeRPC.DeliverStatus.newBuilder().setCode(DeliverStatus.DELIVERED.getCode())
                .setDescription(DeliverStatus.DELIVERED.getDescription())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void handleTransactions(NodeRPC.BatchTransaction request, StreamObserver<NodeRPC.CommonResponse> responseObserver) {
        log.info("Handle transactions from node server");
        Transaction[] transactions = NodeServiceUtils.convertGRPCTransactions(request.getTransactionsList());
        boolean addResult = this.transactionPool.addAll(Arrays.asList(transactions));
        responseObserver.onNext(NodeRPC.CommonResponse.newBuilder().setDescription(addResult ? "Success" : "Failed").build());
        responseObserver.onCompleted();
    }

    public Map handleBroadcastTransactions(Transaction ...transactions) {
        Map<String, Boolean> result = new HashMap<>();
        for (Transaction txn : transactions) {
            String txnId = BlockchainUtils.generateTxId(nodeConfig.getId());
            Transaction transaction;
            try {
                transaction = Transaction.builder()
                        .payload(txn.getPayload())
                        .header(TransactionHeader.builder()
                                .timestamp(System.currentTimeMillis())
                                .txid(txnId)
                                .type(TransactionType.TRANSACTION.name())
                                .status(TransactionStatus.NEW.name())
                                .signature(context.getWallet().getIdentity(IdentityType.NODE).sign(mapper.writeValueAsString(txn.getPayload())))
                                .version("1.0")
                                .creator(context.getWallet().getIdentity(IdentityType.NODE).toString())
                                .build())
                        .build();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            log.info("Transaction:{} added to the blockchain", txnId);
            boolean addTxn = this.transactionPool.add(transaction);
            log.debug("Transaction:{} added to emphasized queue status: {}", txnId, addTxn);
            result.put(txnId, addTxn);
        }
        return result;
    }

}
