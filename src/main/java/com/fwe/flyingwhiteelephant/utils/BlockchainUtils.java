package com.fwe.flyingwhiteelephant.utils;

import java.util.ArrayList;
import java.util.List;

public class BlockchainUtils {
    public static String generateTxId(long nodeId) {
        // generate one snowflake id contains the node id, timestamp and sequence
        int timestampBits = 41;
        int sequenceBits = 18;

        int nodeIdShift = timestampBits + sequenceBits;
        // timestamp from 2024-1-1
        long timestamp = System.currentTimeMillis() - 1704067200000L;
        long sequence = 0;
        return String.valueOf((nodeId << nodeIdShift) | (timestamp << sequenceBits) | sequence);
    }

    public static String parseTxId(String txId) {
        // parse the snowflake id to get the node id, timestamp and sequence
        int timestampBits = 41;
        int sequenceBits = 18;

        long nodeId = Long.parseLong(txId) >> (timestampBits + sequenceBits);
        long timestamp = (Long.parseLong(txId) >> sequenceBits) & ((1L << timestampBits) - 1);
        long sequence = Long.parseLong(txId) & ((1L << sequenceBits) - 1);
        return String.format("nodeId: %d, timestamp: %d, sequence: %d", nodeId, timestamp, sequence);
    }

    // how to construct one Merkle tree
    public static String generateMerkleRoot(List<String> txIds) {
        if (txIds == null || txIds.isEmpty()) {
            return null;
        }
        if (txIds.size() == 1) {
            return txIds.get(0);
        }
        List<String> merkleTree = new ArrayList<>(txIds);
        while (merkleTree.size() > 1) {
            List<String> newMerkleTree = new ArrayList<>();
            for (int i = 0; i < merkleTree.size(); i += 2) {
                String left = merkleTree.get(i);
                String right = (i + 1 < merkleTree.size()) ? merkleTree.get(i + 1) : left;
                newMerkleTree.add(HashUtils.sha256(left + right));
            }
            merkleTree = newMerkleTree;
        }
        return merkleTree.get(0);
    }

    // how to verify the Merkle tree
    public static boolean verifyMerkleRoot(List<String> txIds, String merkleRoot) {
        if (txIds == null || txIds.isEmpty()) {
            return false;
        }
        if (txIds.size() == 1) {
            return txIds.get(0).equals(merkleRoot);
        }
        List<String> merkleTree = new ArrayList<>(txIds);
        while (merkleTree.size() > 1) {
            List<String> newMerkleTree = new ArrayList<>();
            for (int i = 0; i < merkleTree.size(); i += 2) {
                String left = merkleTree.get(i);
                String right = (i + 1 < merkleTree.size()) ? merkleTree.get(i + 1) : left;
                newMerkleTree.add(HashUtils.sha256(left + right));
            }
            merkleTree = newMerkleTree;
        }
        return merkleTree.get(0).equals(merkleRoot);
    }

}
