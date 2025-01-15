package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AppendEntries {
    private int term;
    private int leaderId;
    private List<LogEntry> entries;
}
