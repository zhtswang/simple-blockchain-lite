package com.fwe.flyingwhiteelephant.service.consent.raft;

import lombok.Getter;

@Getter
public enum VoteStatus {
    COMPLETED(2),
    PROGRESS(1),
    START(0),
    INIT(-1);

    private final int status;
    VoteStatus(int status) {
        this.status = status;
    }
}
