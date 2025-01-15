package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppendEntriesResponse {
    private boolean success;
    private int term;
}
