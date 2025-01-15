package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.model.*;
import com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC;
import java.util.Arrays;
import java.util.List;

public final class NodeServiceUtils {
    public static List<NodeRPC.Transaction> convertTransactions(Transaction[] transactions) {
        return Arrays.stream(transactions)
                .map(transaction -> NodeRPC.Transaction.newBuilder()
                        .setHeader(convertTransactionHeader(transaction.getHeader()))
                        .setPayload(convertTransactionPayload(transaction.getPayload()))
                        .build())
                .toList();
    }

    private static NodeRPC.TransactionPayload convertTransactionPayload(TransactionPayload transactionPayload) {
        return NodeRPC.TransactionPayload.newBuilder()
                .setSmartContract(transactionPayload.getSmartContract())
                .addAllArgs(transactionPayload.getArgs())
                .putAllWriteSet(transactionPayload.getWriteSet() == null ? new java.util.HashMap<>() : transactionPayload.getWriteSet())
                .build();
    }

    private static NodeRPC.TransactionHeader convertTransactionHeader(TransactionHeader transactionHeader) {
        return NodeRPC.TransactionHeader.newBuilder()
                .setSignature(transactionHeader.getSignature())
                .setTimestamp(transactionHeader.getTimestamp())
                .setTxid(transactionHeader.getTxid())
                .setType(transactionHeader.getType())
                .setCreator(transactionHeader.getCreator())
                .setVersion(transactionHeader.getVersion())
                .setStatus(transactionHeader.getStatus())
                .build();
    }

    public static NodeRPC.Block convertBlock(Block block) {
        return NodeRPC.Block.newBuilder()
                .setHeader(NodeRPC.Header.newBuilder()
                        .setHeight(block.getHeader().getHeight())
                        .setTimestamp(block.getHeader().getTimestamp())
                        .setHash(block.getHeader().getHash())
                        .setParentHash(block.getHeader().getParentHash() == null ? "" : block.getHeader().getParentHash())
                        .setChannelId(block.getHeader().getChannelId())
                        .setTransactionsRoot(block.getHeader().getTransactionsRoot() == null ? "" : block.getHeader().getTransactionsRoot())
                        .build())
                .addAllTransactions(convertTransactions(block.getTransactions()))
                .setSignature(block.getSignature())
                .setSigner(block.getSigner())
                .build();
    }

    public static List<Block> convertGRPCBlock(List<NodeRPC.Block> rpcBlocks) {
        return rpcBlocks.stream()
                .map(rpcBlock -> Block.builder()
                        .header(BlockHeader.builder()
                                .height(rpcBlock.getHeader().getHeight())
                                .timestamp(rpcBlock.getHeader().getTimestamp())
                                .hash(rpcBlock.getHeader().getHash())
                                .parentHash(rpcBlock.getHeader().getParentHash())
                                .channelId(rpcBlock.getHeader().getChannelId())
                                .transactionsRoot(rpcBlock.getHeader().getTransactionsRoot())
                                .build())
                        .transactions(convertGRPCTransactions(rpcBlock.getTransactionsList()))
                        .signature(rpcBlock.getSignature())
                        .signer(rpcBlock.getSigner())
                        .build())
                .toList();
    }

    public static Transaction[] convertGRPCTransactions(List<NodeRPC.Transaction> transactionsList) {
        return transactionsList.stream()
                .map(rpcTransaction -> Transaction.builder()
                        .header(convertGRPCTransactionHeader(rpcTransaction.getHeader()))
                        .payload(convertGRPCTransactionPayload(rpcTransaction.getPayload()))
                        .build())
                .toArray(Transaction[]::new);
    }

    private static TransactionPayload convertGRPCTransactionPayload(NodeRPC.TransactionPayload payload) {
        return TransactionPayload.builder()
                .smartContract(payload.getSmartContract())
                .args(payload.getArgsList())
                .writeSet(payload.getWriteSetMap())
                .build();
    }

    private static TransactionHeader convertGRPCTransactionHeader(NodeRPC.TransactionHeader rpcDataHeader) {
        return TransactionHeader.builder()
                .signature(rpcDataHeader.getSignature())
                .timestamp(rpcDataHeader.getTimestamp())
                .txid(rpcDataHeader.getTxid())
                .type(rpcDataHeader.getType())
                .creator(rpcDataHeader.getCreator())
                .version(rpcDataHeader.getVersion())
                .status(rpcDataHeader.getStatus())
                .build();
    }
}
