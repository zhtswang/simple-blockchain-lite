package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.enums.DeliverStatus;
import com.fwe.flyingwhiteelephant.model.Block;
import com.fwe.flyingwhiteelephant.model.Node;
import com.fwe.flyingwhiteelephant.model.Transaction;
import com.fwe.flyingwhiteelephant.service.rpc.node.BlockServiceGrpc;
import com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public record NodeClient(Node nodeConfig) {

    public <R> Optional<R> channelTemplate(Function<BlockServiceGrpc.BlockServiceBlockingStub, R> callback) {
        ManagedChannel channel = null;
        URI uri = nodeConfig.getEndpoint().getUri();
        try {
            channel = ManagedChannelBuilder.forAddress(uri.getHost(), uri.getPort() + 3000)
                    .usePlaintext()
                    .build();
            BlockServiceGrpc.BlockServiceBlockingStub blockingStub = BlockServiceGrpc.newBlockingStub(channel).withWaitForReady();
            return Optional.of(callback.apply(blockingStub));
        } catch (Exception e) {
            log.error("Node client sends request to peer {}:{} error", uri.getHost(), uri.getPort() + 3000, e);
            // how to return a default value
            return Optional.empty();
        } finally {
            if (channel != null) {
                channel.shutdown();
                try {
                    if (channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        channel.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    channel.shutdownNow();
                }
            }
        }
    }

    public void deliverBlocks(Block block) {
        // send the block to the endpoint
        log.info("Deliver block: {} to node: {}", block.getHeader().getHeight(), nodeConfig.getId());
        channelTemplate(blockingStub -> {
            var rpcResp = blockingStub.handleDeliverBlock(NodeServiceUtils.convertBlock(block));
            log.info("Deliver block: {} response from node: {} is: {}", block.getHeader().getHeight(), nodeConfig.getId(), rpcResp.getDescription());
            return rpcResp;
        });
    }

    public Optional<DeliverStatus> deliverConsentRequest(Block block) {
        // send the block to the endpoint
        log.info("Deliver consent request to node: {}, block: {}", nodeConfig.getId(), block.getHeader().getHeight());
        return channelTemplate(blockingStub -> {
            var grpcResp = blockingStub.handleConsentRequest(NodeServiceUtils.convertBlock(block));
            log.info("Deliver consent request response from node: {}, block: {} is: {}", nodeConfig.getId(), block.getHeader().getHeight(), grpcResp.getDescription());
            return DeliverStatus.fromDescription(grpcResp.getDescription());
        });
    }

    public Optional<String> sendTransactions(Transaction[] txs) {
        return channelTemplate(blockingStub -> {
            var grpcResp = blockingStub.handleTransactions(NodeRPC.BatchTransaction.newBuilder()
                    .addAllTransactions(NodeServiceUtils.convertTransactions(txs))
                    .build());
            log.info("Send transactions response from node: {} is: {}", nodeConfig.getId(), grpcResp.getDescription());
            return grpcResp.getDescription();
        });
    }

    public Optional<Long> getLatestHeight() {
        return channelTemplate(blockingStub -> {
            var grpcResp = blockingStub.handleGetLatestBlockHeight(NodeRPC.BlockHeightRequest.newBuilder().setNodeId(nodeConfig.getId()).build());
            log.info("Get latest block height response from node: {} is: {}", nodeConfig.getId(), grpcResp.getHeight());
            return grpcResp.getHeight();
        });
    }

    public Optional<List<Block>> getBlocks(Long start, Long end) {
        return channelTemplate(blockingStub -> {
            var grpcResp = blockingStub.handleGetBlockStream(NodeRPC.BlockRequest.newBuilder()
                    .setStartBlockNumber(start)
                    .setEndBlockNumber(end)
                    .build());
            List<Block> blocks = new ArrayList<>();
            grpcResp.forEachRemaining(blockResp -> blocks.addAll(NodeServiceUtils.convertGRPCBlock(blockResp.getBlocksList())));
            return blocks;
        });
    }
}
