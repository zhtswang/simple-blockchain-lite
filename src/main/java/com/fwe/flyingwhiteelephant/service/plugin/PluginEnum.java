package com.fwe.flyingwhiteelephant.service.plugin;

import lombok.Getter;

@Getter
public enum PluginEnum {
    DEFAULT("default"),
    DID("did");

    private final String name;

    PluginEnum(String name) {
        this.name = name;
    }

}
