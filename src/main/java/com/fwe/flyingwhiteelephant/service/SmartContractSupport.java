package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.service.plugin.PluginClient;
import com.fwe.flyingwhiteelephant.spi.CCRequest;
import com.fwe.flyingwhiteelephant.spi.CCResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SmartContractSupport {

    private final PluginClient pluginClient;

    public SmartContractSupport(PluginClient pluginClient) {
        this.pluginClient = pluginClient;
    }

    // call the smart contract by the plugin with domain and port

    public Map<String, String> call(String contractAddress, String contractVersion, List<String> args)  {
        // call the method
        String funcName = args.get(0);
        String key = args.get(1);
        String value = args.get(2);
        CCRequest ccRequest = new CCRequest();
        ccRequest.setPluginName(contractAddress);
        ccRequest.setMethod(funcName);
        ccRequest.setParams(Map.of("key", key, "value", value));
        ccRequest.setVersion(contractVersion);
        ccRequest.setName(contractAddress);
        CCResponse ccResponse = null;
        log.info("Start to execute the smart contract {} method: {}", contractAddress, funcName);
        ccResponse = pluginClient.callPlugin(ccRequest);
        // execute the smart contract
        log.info("End to execute the Smart contract {} method: {} result: {}", contractAddress, funcName, ccResponse.getResult());
        // TODO: how to implement it?
        return Map.of("status", ccResponse.getStatus(), "result", ccResponse.getResult());
    }
}
