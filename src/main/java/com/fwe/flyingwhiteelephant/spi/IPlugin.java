package com.fwe.flyingwhiteelephant.spi;

public interface IPlugin {
    CCResponse call(CCRequest ccRequest);
    void storeState(String key, String value);
    String readState(String key);
}
