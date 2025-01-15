package com.fwe.flyingwhiteelephant.service.plugin;

import com.fwe.flyingwhiteelephant.service.spi.plugin.CallGrpc;
import com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin;
import com.fwe.flyingwhiteelephant.spi.CCRequest;
import com.fwe.flyingwhiteelephant.spi.CCResponse;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PluginClient {

    private final String serverRPCDomain;
    private final int serverRPCPort;
    private String certPath;
    private String privateKeyPath;

    public PluginClient(String serverRPCDomain, int serverRPCPort) {
       this.serverRPCDomain = serverRPCDomain;
       this.serverRPCPort = serverRPCPort;
    }

    public PluginClient(String serverRPCDomain, int serverRPCPort, final String certPath, final String privateKeyPath) {
        this(serverRPCDomain, serverRPCPort);
        this.certPath = certPath;
        this.privateKeyPath = privateKeyPath;
    }
    // call plugin server execute
    public CCResponse callPlugin(CCRequest ccRequest) {
        log.info("Calling plugin server, serverRPCDomain: {}, serverRPCPort: {}", serverRPCDomain, serverRPCPort);
        // create a channel
        ManagedChannel channel = null;
        try {
            channel = NettyChannelBuilder.forAddress(serverRPCDomain, serverRPCPort)
                    .useTransportSecurity()
                    .sslContext(GrpcSslContexts.forClient()
                            .trustManager(new File(certPath))
                            .keyManager(new File(certPath), new File(privateKeyPath))
                            .build())
                    .build();
            // create a blocking stub
            var blockingStub = CallGrpc.newBlockingStub(channel).withDeadlineAfter(30, TimeUnit.SECONDS).withWaitForReady();
            log.info("Init the blocking stub to call the plugin server, timeout: 30 seconds");
            // create a request
            var request = Plugin.CCRequest.newBuilder()
                    .setPluginName(ccRequest.getPluginName())
                    .setMethod(ccRequest.getMethod())
                    .setVersion(ccRequest.getVersion())
                    .setName(ccRequest.getName())
                    .putAllParams(ccRequest.getParams())
                    .build();
            // call the server
            var response =  blockingStub.execute(request);
            // create a response
            var ccResponse = new CCResponse();
            ccResponse.setStatus(response.getStatus());
            ccResponse.setResult(response.getResult());
            // return the response
            return ccResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                channel.shutdown();
                try {
                    if (channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        channel.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    channel.shutdownNow();
                }
            }
        }
    }
}
