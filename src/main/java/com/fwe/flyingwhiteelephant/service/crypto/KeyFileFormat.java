package com.fwe.flyingwhiteelephant.service.crypto;

import lombok.Getter;

@Getter
public enum KeyFileFormat {
    CERTIFICATE("CERTIFICATE"),
    PRIVATE_KEY("PRIVATE KEY"),
    ;

    private final String description;

    KeyFileFormat(String description) {
        this.description = description;
    }

}
