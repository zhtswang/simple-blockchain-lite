package com.fwe.flyingwhiteelephant.service.plugin;


import com.fwe.flyingwhiteelephant.service.spi.plugin.CallGrpc;
import com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin;
import com.fwe.flyingwhiteelephant.spi.CCRequest;
import com.fwe.flyingwhiteelephant.spi.CCResponse;
import com.fwe.flyingwhiteelephant.spi.IPlugin;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.ClientAuth;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PluginServer extends CallGrpc.CallImplBase {
    private final Map<String, IPlugin> plugins = new HashMap<>();
    public PluginServer(int port) {
        // start the rpc server and enable tls
        log.info("Starting plugin server on port: {}", port);
        var serverBuilder = ServerBuilder.forPort(port).addService(this).build();
        try {
            serverBuilder.start();
        } catch (Exception e) {
            log.error("Error starting plugin server", e);
        }
    }

    public PluginServer(int port, String certChainPath, String privateKeyPath) {
        // start the rpc server and enable tls
        log.info("Starting plugin server on port: {} with tls: true", port);
        try(InputStream certChainStream = Files.newInputStream(Paths.get(certChainPath));
            InputStream privateKeyStream = Files.newInputStream(Paths.get(privateKeyPath))) {
            var serverBuilder = NettyServerBuilder
                    .forPort(port)
                    .sslContext(GrpcSslContexts.forServer(certChainStream, privateKeyStream)
                            .clientAuth(ClientAuth.NONE)
                            .build())
                    .addService(this).build();
            serverBuilder.start();
        } catch (Exception e) {
            log.error("Error starting plugin server", e);
        }
    }

    public void loadSystemPlugins() {
        // load the system plugins
        plugins.put(PluginEnum.DID.name(), new DIDPlugin());
        plugins.put(PluginEnum.DEFAULT.name(), new DefaultPlugin());
    }

    public void loadExternalPlugins(String pluginName, IPlugin plugin) {
        // load the plugin
        plugins.put(pluginName, plugin);
    }

    @Override
    public void execute(Plugin.CCRequest request, StreamObserver<Plugin.CCResponse> responseObserver) {
        // get the plugin
        IPlugin plugin = plugins.getOrDefault(request.getPluginName(), new DefaultPlugin());
        CCRequest ccRequest = new CCRequest();
        ccRequest.setPluginName(request.getPluginName());
        ccRequest.setParams(request.getParamsMap());
        ccRequest.setMethod(request.getMethod());
        ccRequest.setVersion(request.getVersion());
        ccRequest.setName(request.getName());

        CCResponse response = plugin.call(ccRequest);
        responseObserver.onNext(Plugin.CCResponse.newBuilder()
                .setStatus(response.getStatus())
                .setResult(response.getResult())
                .build());
        responseObserver.onCompleted();
    }
}
