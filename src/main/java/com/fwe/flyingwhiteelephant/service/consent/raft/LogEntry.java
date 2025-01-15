package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class LogEntry {
    private int term;
    private String command;
}
