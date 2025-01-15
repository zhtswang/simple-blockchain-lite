package com.fwe.flyingwhiteelephant.service.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.service.AppContextUtil;
import com.fwe.flyingwhiteelephant.service.BlockchainStorageService;
import com.fwe.flyingwhiteelephant.spi.CCRequest;
import com.fwe.flyingwhiteelephant.spi.CCResponse;
import com.fwe.flyingwhiteelephant.spi.IPlugin;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DefaultPlugin  implements IPlugin {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public CCResponse call(CCRequest ccRequest) {
        try {
            return (CCResponse) this.getClass().getDeclaredMethod(ccRequest.getMethod(), CCRequest.class).invoke(this, ccRequest);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void storeState(String key, String value) {
        AppContextUtil.getBean(BlockchainStorageService.class).storeState(key, value);
    }

    @Override
    public String readState(String key) {
        return AppContextUtil.getBean(BlockchainStorageService.class).readState(key);
    }

    public CCResponse write(CCRequest ccRequest) throws JsonProcessingException {
        log.info("write the key and value to the state DB.");
        for(String key : ccRequest.getParams().keySet()) {
            storeState(key, ccRequest.getParams().get(key));
        }
        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(objectMapper.writeValueAsString(ccRequest.getParams()));
        return response;
    }

    public CCResponse read(CCRequest ccRequest) throws JsonProcessingException {
        log.info("read method start");
        Map<String, String> keyValues = new HashMap<>();
        for(String key : ccRequest.getParams().keySet()) {
            log.info("key: {}, value: {}", key, ccRequest.getParams().get(key));
            String value = readState(key);
            keyValues.put(key, value);
        }
        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(objectMapper.writeValueAsString(keyValues));
        return response;
    }
}
