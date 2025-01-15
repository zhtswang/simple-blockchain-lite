package com.fwe.flyingwhiteelephant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Block {
    private BlockHeader header;
    private Transaction[] transactions;
    private String signature;
    private String signer;
}
