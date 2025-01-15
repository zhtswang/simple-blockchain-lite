package com.fwe.flyingwhiteelephant.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Node {

  private NodeEndpoint endpoint;
  private Long id;
  private String pluginServerCertChainPath;
  private String pluginServerPrivateKeyPath;
  private String pluginClientCertChainPath;
  private String pluginClientPrivateKeyPath;
  private Boolean tls;
}
