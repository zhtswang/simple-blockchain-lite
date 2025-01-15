package com.fwe.flyingwhiteelephant.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliverStatus {
    PENDING(0, "pending"),
    DELIVERED(1, "delivered"),
    FAILED(0, "failed");
    private final int code;
    private final String description;

    // get enum by description
    public static DeliverStatus fromDescription(String description) {
        for (DeliverStatus deliverStatus : DeliverStatus.values()) {
            if (deliverStatus.getDescription().equals(description)) {
                return deliverStatus;
            }
        }
        return FAILED;
    }
}
