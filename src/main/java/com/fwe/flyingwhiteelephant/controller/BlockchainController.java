package com.fwe.flyingwhiteelephant.controller;

import com.fwe.flyingwhiteelephant.model.Block;
import com.fwe.flyingwhiteelephant.service.Blockchain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping(value ="/api/v1/blockchain")
@RestController
public class BlockchainController {
  @Resource
  private Blockchain blockchain;

  @GetMapping(value = "/node/{id}/block/latest-height")
  public long getLatestHeight(@PathVariable("id") String leaderNodeId) {
      log.info("Get latest block height from leader node:{}", leaderNodeId);
      return blockchain.getLatestBlockHeight();
  }

    @GetMapping(value = "/node/{id}/block")
    public List<Block> getBlocks(@PathVariable("id") String nodeId, @RequestParam("from") long from, @RequestParam("to") long to) {
        log.info("Get all blocks from node:{}", nodeId);
        return blockchain.getBlocks(from, to);
    }
}
