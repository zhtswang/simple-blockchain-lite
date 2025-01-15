package com.fwe.flyingwhiteelephant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BlockHeader {
    private String hash;
    private String parentHash;
    private String transactionsRoot;
    private long height;
    private long timestamp;
    private String channelId;
}
