package com.fwe.flyingwhiteelephant.controller;

import com.fwe.flyingwhiteelephant.service.Blockchain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RequestMapping(value ="/api/blockchain")
@RestController
public class BlockchainController {
  @Resource
  private Blockchain blockchain;

  @GetMapping(value = "/latest-height")
  public ResponseEntity<?> getLatestHeight() {
      return new ResponseEntity<>(blockchain.getLatestBlockHeight(), HttpStatus.OK);
  }

    @GetMapping(value = "/blocks")
    public ResponseEntity<?> getBlocks(@RequestParam("from") long from, @RequestParam("to") long to) {
        return new ResponseEntity<>(blockchain.getBlocks(from, to), HttpStatus.OK);
    }

    @PostMapping(value = "/user/enroll")
    public ResponseEntity<?> enroll(@RequestBody Map<String, String> userMap) {
        return new ResponseEntity<>(blockchain.enroll(userMap.get("userName")), HttpStatus.OK);
    }
}
