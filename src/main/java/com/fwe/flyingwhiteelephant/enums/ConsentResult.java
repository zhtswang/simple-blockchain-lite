package com.fwe.flyingwhiteelephant.enums;

public enum ConsentResult {
    SUCCESS("success"),
    FAIL("fail"),
    TIMEOUT("timeout");

    private final String value;

    ConsentResult(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
