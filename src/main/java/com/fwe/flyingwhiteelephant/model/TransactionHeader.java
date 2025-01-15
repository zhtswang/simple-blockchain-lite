package com.fwe.flyingwhiteelephant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionHeader {
    private String txid;
    private Long timestamp;
    private String creator;
    private String signature;
    private String version;
    private String type;
    private String status;
}
