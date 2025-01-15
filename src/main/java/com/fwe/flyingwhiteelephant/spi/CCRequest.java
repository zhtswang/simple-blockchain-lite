package com.fwe.flyingwhiteelephant.spi;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CCRequest {
    private String pluginName;
    private String name;
    private String version;
    private String method;
    private Map<String, String> params;

}
