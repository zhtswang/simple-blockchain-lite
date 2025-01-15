package com.fwe.flyingwhiteelephant.spi;


import com.fwe.flyingwhiteelephant.service.BlockchainStorageService;

public interface IPlugin {
    CCResponse call(CCRequest ccRequest);
    void storeState(String key, String value);
    String readState(String key);
}
