package com.fwe.flyingwhiteelephant.config;

import com.fwe.flyingwhiteelephant.model.Node;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "blockchain.peers")
public class NodeConfig {
    private List<Node> nodes;
    private String algorithm;
}
