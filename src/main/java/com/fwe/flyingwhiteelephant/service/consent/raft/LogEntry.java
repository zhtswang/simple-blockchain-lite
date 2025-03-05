package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LogEntry {
    private int term;
    private String command;
    private long index;
    private long timestamp;
    
    public boolean isValid() {
        return term >= 0 && index >= 0 && command != null;
    }
    
    @Override
    public String toString() {
        return String.format("LogEntry{index=%d, term=%d, command='%s'}", index, term, command);
    }
}
