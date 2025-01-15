package com.fwe.flyingwhiteelephant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionPayload {
    private String smartContract;
    private List<String> args;
    private Map<String, String> writeSet;
}
