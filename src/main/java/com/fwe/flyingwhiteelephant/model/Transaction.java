package com.fwe.flyingwhiteelephant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Transaction {
    private TransactionHeader header;
    private TransactionPayload payload;
}
