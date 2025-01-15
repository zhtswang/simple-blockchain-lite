package com.fwe.flyingwhiteelephant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.model.Block;
import org.iq80.leveldb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
@Service
public class BlockchainStorageService {
    @Value("${blockchain.chain_path}${node.id}")
    private String levelDBPath;

    @Value("${blockchain.chain_path}${node.id}/state_db")
    private String stateDBPath;

    public void storeBlock(Block block) {
        // save data into levelDB
        Options options = new Options();
        options.createIfMissing(true);
        ObjectMapper mapper = new ObjectMapper();
        String channelHeight = String.valueOf(block.getHeader().getHeight());
        synchronized (this) {
            try (DB levelDB = factory.open(new File(levelDBPath), options)) {
                levelDB.put(channelHeight.getBytes(), mapper.writeValueAsBytes(block));
                // update the latest block height
                levelDB.put("latest".getBytes(), channelHeight.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void storeState(String key, String value) {
        Options options = new Options();
        options.createIfMissing(true);
        synchronized (this) {
            try (DB levelDB = factory.open(new File(stateDBPath), options)) {
                levelDB.put(key.getBytes(), value.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String readState(String key) {
        Options options = new Options();
        options.createIfMissing(true);
        synchronized (this) {
            try (DB levelDB = factory.open(new File(stateDBPath), options)) {
                byte[] value = levelDB.get(key.getBytes());
                if (value != null) {
                    return new String(value);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public Block getBlockByHeight(long l) {
        Options options = new Options();
        options.createIfMissing(true);
        ObjectMapper mapper = new ObjectMapper();
        synchronized (this) {
            try (DB levelDB = factory.open(new File(levelDBPath), options)) {
                byte[] blockbytes = levelDB.get(String.valueOf(l).getBytes());
                if (blockbytes != null) {
                    return mapper.readValue(blockbytes, Block.class);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public long getLatestBlockHeight() {
        Options options = new Options();
        options.createIfMissing(true);
        synchronized (this) {
            try (DB levelDB = factory.open(new File(levelDBPath), options)) {
                byte[] latest = levelDB.get("latest".getBytes());
                if (latest != null) {
                    return Long.parseLong(new String(latest));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }
}
