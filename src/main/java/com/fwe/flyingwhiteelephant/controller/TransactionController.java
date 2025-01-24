package com.fwe.flyingwhiteelephant.controller;

import com.fwe.flyingwhiteelephant.model.Transaction;
import com.fwe.flyingwhiteelephant.service.Blockchain;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/transactions")
public class TransactionController {

    @Resource
    Blockchain blockchain;
    /***
     * This method is used to receive the transactions from the client and broadcast it to the network
     * @param transactions
     * @return
     */
    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody Transaction[] transactions) {
        return new ResponseEntity<>(blockchain.broadcast(transactions), HttpStatus.OK);
    }

}
