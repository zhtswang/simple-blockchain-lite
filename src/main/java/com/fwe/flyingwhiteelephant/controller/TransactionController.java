package com.fwe.flyingwhiteelephant.controller;

import com.fwe.flyingwhiteelephant.model.Transaction;
import com.fwe.flyingwhiteelephant.service.Blockchain;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

    @Resource
    Blockchain blockchain;
    /***
     * This method is used to receive the transactions from the client and broadcast it to the network
     * @param transactions
     * @return
     */
    @PostMapping("/broadcast")
    public String broadcast(@RequestBody Transaction[] transactions) {
        blockchain.broadcast(transactions);
        return "success";
    }

}
