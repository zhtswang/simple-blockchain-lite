package com.fwe.flyingwhiteelephant.service.rpc.node;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.1)",
    comments = "Source: Node.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class BlockServiceGrpc {

  private BlockServiceGrpc() {}

  public static final String SERVICE_NAME = "BlockService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse> getHandleGetBlockStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleGetBlockStream",
      requestType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest.class,
      responseType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse> getHandleGetBlockStreamMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse> getHandleGetBlockStreamMethod;
    if ((getHandleGetBlockStreamMethod = BlockServiceGrpc.getHandleGetBlockStreamMethod) == null) {
      synchronized (BlockServiceGrpc.class) {
        if ((getHandleGetBlockStreamMethod = BlockServiceGrpc.getHandleGetBlockStreamMethod) == null) {
          BlockServiceGrpc.getHandleGetBlockStreamMethod = getHandleGetBlockStreamMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleGetBlockStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BlockServiceMethodDescriptorSupplier("handleGetBlockStream"))
              .build();
        }
      }
    }
    return getHandleGetBlockStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight> getHandleGetLatestBlockHeightMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleGetLatestBlockHeight",
      requestType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest.class,
      responseType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight> getHandleGetLatestBlockHeightMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight> getHandleGetLatestBlockHeightMethod;
    if ((getHandleGetLatestBlockHeightMethod = BlockServiceGrpc.getHandleGetLatestBlockHeightMethod) == null) {
      synchronized (BlockServiceGrpc.class) {
        if ((getHandleGetLatestBlockHeightMethod = BlockServiceGrpc.getHandleGetLatestBlockHeightMethod) == null) {
          BlockServiceGrpc.getHandleGetLatestBlockHeightMethod = getHandleGetLatestBlockHeightMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleGetLatestBlockHeight"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight.getDefaultInstance()))
              .setSchemaDescriptor(new BlockServiceMethodDescriptorSupplier("handleGetLatestBlockHeight"))
              .build();
        }
      }
    }
    return getHandleGetLatestBlockHeightMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> getHandleConsentRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleConsentRequest",
      requestType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block.class,
      responseType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> getHandleConsentRequestMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> getHandleConsentRequestMethod;
    if ((getHandleConsentRequestMethod = BlockServiceGrpc.getHandleConsentRequestMethod) == null) {
      synchronized (BlockServiceGrpc.class) {
        if ((getHandleConsentRequestMethod = BlockServiceGrpc.getHandleConsentRequestMethod) == null) {
          BlockServiceGrpc.getHandleConsentRequestMethod = getHandleConsentRequestMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleConsentRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus.getDefaultInstance()))
              .setSchemaDescriptor(new BlockServiceMethodDescriptorSupplier("handleConsentRequest"))
              .build();
        }
      }
    }
    return getHandleConsentRequestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> getHandleDeliverBlockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleDeliverBlock",
      requestType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block.class,
      responseType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> getHandleDeliverBlockMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> getHandleDeliverBlockMethod;
    if ((getHandleDeliverBlockMethod = BlockServiceGrpc.getHandleDeliverBlockMethod) == null) {
      synchronized (BlockServiceGrpc.class) {
        if ((getHandleDeliverBlockMethod = BlockServiceGrpc.getHandleDeliverBlockMethod) == null) {
          BlockServiceGrpc.getHandleDeliverBlockMethod = getHandleDeliverBlockMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleDeliverBlock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus.getDefaultInstance()))
              .setSchemaDescriptor(new BlockServiceMethodDescriptorSupplier("handleDeliverBlock"))
              .build();
        }
      }
    }
    return getHandleDeliverBlockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse> getHandleTransactionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleTransactions",
      requestType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction.class,
      responseType = com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction,
      com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse> getHandleTransactionsMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse> getHandleTransactionsMethod;
    if ((getHandleTransactionsMethod = BlockServiceGrpc.getHandleTransactionsMethod) == null) {
      synchronized (BlockServiceGrpc.class) {
        if ((getHandleTransactionsMethod = BlockServiceGrpc.getHandleTransactionsMethod) == null) {
          BlockServiceGrpc.getHandleTransactionsMethod = getHandleTransactionsMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction, com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleTransactions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BlockServiceMethodDescriptorSupplier("handleTransactions"))
              .build();
        }
      }
    }
    return getHandleTransactionsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BlockServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BlockServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BlockServiceStub>() {
        @java.lang.Override
        public BlockServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BlockServiceStub(channel, callOptions);
        }
      };
    return BlockServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BlockServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BlockServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BlockServiceBlockingStub>() {
        @java.lang.Override
        public BlockServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BlockServiceBlockingStub(channel, callOptions);
        }
      };
    return BlockServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BlockServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BlockServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BlockServiceFutureStub>() {
        @java.lang.Override
        public BlockServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BlockServiceFutureStub(channel, callOptions);
        }
      };
    return BlockServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class BlockServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void handleGetBlockStream(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleGetBlockStreamMethod(), responseObserver);
    }

    /**
     */
    public void handleGetLatestBlockHeight(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleGetLatestBlockHeightMethod(), responseObserver);
    }

    /**
     */
    public void handleConsentRequest(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleConsentRequestMethod(), responseObserver);
    }

    /**
     */
    public void handleDeliverBlock(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleDeliverBlockMethod(), responseObserver);
    }

    /**
     */
    public void handleTransactions(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleTransactionsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHandleGetBlockStreamMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest,
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse>(
                  this, METHODID_HANDLE_GET_BLOCK_STREAM)))
          .addMethod(
            getHandleGetLatestBlockHeightMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest,
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight>(
                  this, METHODID_HANDLE_GET_LATEST_BLOCK_HEIGHT)))
          .addMethod(
            getHandleConsentRequestMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block,
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus>(
                  this, METHODID_HANDLE_CONSENT_REQUEST)))
          .addMethod(
            getHandleDeliverBlockMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block,
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus>(
                  this, METHODID_HANDLE_DELIVER_BLOCK)))
          .addMethod(
            getHandleTransactionsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction,
                com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse>(
                  this, METHODID_HANDLE_TRANSACTIONS)))
          .build();
    }
  }

  /**
   */
  public static final class BlockServiceStub extends io.grpc.stub.AbstractAsyncStub<BlockServiceStub> {
    private BlockServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BlockServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BlockServiceStub(channel, callOptions);
    }

    /**
     */
    public void handleGetBlockStream(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getHandleGetBlockStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleGetLatestBlockHeight(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleGetLatestBlockHeightMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleConsentRequest(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleConsentRequestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleDeliverBlock(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleDeliverBlockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleTransactions(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleTransactionsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class BlockServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<BlockServiceBlockingStub> {
    private BlockServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BlockServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BlockServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse> handleGetBlockStream(
        com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getHandleGetBlockStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight handleGetLatestBlockHeight(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleGetLatestBlockHeightMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus handleConsentRequest(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleConsentRequestMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus handleDeliverBlock(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleDeliverBlockMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse handleTransactions(com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleTransactionsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class BlockServiceFutureStub extends io.grpc.stub.AbstractFutureStub<BlockServiceFutureStub> {
    private BlockServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BlockServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BlockServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight> handleGetLatestBlockHeight(
        com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleGetLatestBlockHeightMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> handleConsentRequest(
        com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleConsentRequestMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus> handleDeliverBlock(
        com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleDeliverBlockMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse> handleTransactions(
        com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleTransactionsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_HANDLE_GET_BLOCK_STREAM = 0;
  private static final int METHODID_HANDLE_GET_LATEST_BLOCK_HEIGHT = 1;
  private static final int METHODID_HANDLE_CONSENT_REQUEST = 2;
  private static final int METHODID_HANDLE_DELIVER_BLOCK = 3;
  private static final int METHODID_HANDLE_TRANSACTIONS = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BlockServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BlockServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_GET_BLOCK_STREAM:
          serviceImpl.handleGetBlockStream((com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockRequest) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockResponse>) responseObserver);
          break;
        case METHODID_HANDLE_GET_LATEST_BLOCK_HEIGHT:
          serviceImpl.handleGetLatestBlockHeight((com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeightRequest) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BlockHeight>) responseObserver);
          break;
        case METHODID_HANDLE_CONSENT_REQUEST:
          serviceImpl.handleConsentRequest((com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus>) responseObserver);
          break;
        case METHODID_HANDLE_DELIVER_BLOCK:
          serviceImpl.handleDeliverBlock((com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.Block) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.DeliverStatus>) responseObserver);
          break;
        case METHODID_HANDLE_TRANSACTIONS:
          serviceImpl.handleTransactions((com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.BatchTransaction) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.CommonResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class BlockServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BlockServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.fwe.flyingwhiteelephant.service.rpc.node.NodeRPC.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BlockService");
    }
  }

  private static final class BlockServiceFileDescriptorSupplier
      extends BlockServiceBaseDescriptorSupplier {
    BlockServiceFileDescriptorSupplier() {}
  }

  private static final class BlockServiceMethodDescriptorSupplier
      extends BlockServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BlockServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BlockServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BlockServiceFileDescriptorSupplier())
              .addMethod(getHandleGetBlockStreamMethod())
              .addMethod(getHandleGetLatestBlockHeightMethod())
              .addMethod(getHandleConsentRequestMethod())
              .addMethod(getHandleDeliverBlockMethod())
              .addMethod(getHandleTransactionsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
