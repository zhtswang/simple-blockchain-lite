package com.fwe.flyingwhiteelephant.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BlockchainStartup implements ApplicationRunner {
    @Resource
    private Blockchain blockchain;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application startup with args {}", args);
        blockchain.start();
    }
}
