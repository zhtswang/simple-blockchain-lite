package com.fwe.flyingwhiteelephant.service.consent.raft.protocol;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.1)",
    comments = "Source: Raft.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ConsentGrpc {

  private ConsentGrpc() {}

  public static final String SERVICE_NAME = "Consent";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest,
      com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse> getHandleRequestVoteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleRequestVote",
      requestType = com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest.class,
      responseType = com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest,
      com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse> getHandleRequestVoteMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest, com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse> getHandleRequestVoteMethod;
    if ((getHandleRequestVoteMethod = ConsentGrpc.getHandleRequestVoteMethod) == null) {
      synchronized (ConsentGrpc.class) {
        if ((getHandleRequestVoteMethod = ConsentGrpc.getHandleRequestVoteMethod) == null) {
          ConsentGrpc.getHandleRequestVoteMethod = getHandleRequestVoteMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest, com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleRequestVote"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ConsentMethodDescriptorSupplier("handleRequestVote"))
              .build();
        }
      }
    }
    return getHandleRequestVoteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest,
      com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse> getHandleAppendEntriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleAppendEntries",
      requestType = com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest.class,
      responseType = com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest,
      com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse> getHandleAppendEntriesMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest, com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse> getHandleAppendEntriesMethod;
    if ((getHandleAppendEntriesMethod = ConsentGrpc.getHandleAppendEntriesMethod) == null) {
      synchronized (ConsentGrpc.class) {
        if ((getHandleAppendEntriesMethod = ConsentGrpc.getHandleAppendEntriesMethod) == null) {
          ConsentGrpc.getHandleAppendEntriesMethod = getHandleAppendEntriesMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest, com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleAppendEntries"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ConsentMethodDescriptorSupplier("handleAppendEntries"))
              .build();
        }
      }
    }
    return getHandleAppendEntriesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest,
      com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse> getHandleHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "handleHeartbeat",
      requestType = com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest.class,
      responseType = com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest,
      com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse> getHandleHeartbeatMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest, com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse> getHandleHeartbeatMethod;
    if ((getHandleHeartbeatMethod = ConsentGrpc.getHandleHeartbeatMethod) == null) {
      synchronized (ConsentGrpc.class) {
        if ((getHandleHeartbeatMethod = ConsentGrpc.getHandleHeartbeatMethod) == null) {
          ConsentGrpc.getHandleHeartbeatMethod = getHandleHeartbeatMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest, com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handleHeartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ConsentMethodDescriptorSupplier("handleHeartbeat"))
              .build();
        }
      }
    }
    return getHandleHeartbeatMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ConsentStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConsentStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConsentStub>() {
        @java.lang.Override
        public ConsentStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConsentStub(channel, callOptions);
        }
      };
    return ConsentStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ConsentBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConsentBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConsentBlockingStub>() {
        @java.lang.Override
        public ConsentBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConsentBlockingStub(channel, callOptions);
        }
      };
    return ConsentBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ConsentFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConsentFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConsentFutureStub>() {
        @java.lang.Override
        public ConsentFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConsentFutureStub(channel, callOptions);
        }
      };
    return ConsentFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ConsentImplBase implements io.grpc.BindableService {

    /**
     */
    public void handleRequestVote(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleRequestVoteMethod(), responseObserver);
    }

    /**
     */
    public void handleAppendEntries(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleAppendEntriesMethod(), responseObserver);
    }

    /**
     */
    public void handleHeartbeat(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleHeartbeatMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHandleRequestVoteMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest,
                com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse>(
                  this, METHODID_HANDLE_REQUEST_VOTE)))
          .addMethod(
            getHandleAppendEntriesMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest,
                com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse>(
                  this, METHODID_HANDLE_APPEND_ENTRIES)))
          .addMethod(
            getHandleHeartbeatMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest,
                com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse>(
                  this, METHODID_HANDLE_HEARTBEAT)))
          .build();
    }
  }

  /**
   */
  public static final class ConsentStub extends io.grpc.stub.AbstractAsyncStub<ConsentStub> {
    private ConsentStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConsentStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConsentStub(channel, callOptions);
    }

    /**
     */
    public void handleRequestVote(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleRequestVoteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleAppendEntries(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleAppendEntriesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleHeartbeat(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ConsentBlockingStub extends io.grpc.stub.AbstractBlockingStub<ConsentBlockingStub> {
    private ConsentBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConsentBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConsentBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse handleRequestVote(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleRequestVoteMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse handleAppendEntries(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleAppendEntriesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse handleHeartbeat(com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ConsentFutureStub extends io.grpc.stub.AbstractFutureStub<ConsentFutureStub> {
    private ConsentFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConsentFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConsentFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse> handleRequestVote(
        com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleRequestVoteMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse> handleAppendEntries(
        com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleAppendEntriesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse> handleHeartbeat(
        com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleHeartbeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_HANDLE_REQUEST_VOTE = 0;
  private static final int METHODID_HANDLE_APPEND_ENTRIES = 1;
  private static final int METHODID_HANDLE_HEARTBEAT = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ConsentImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ConsentImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_REQUEST_VOTE:
          serviceImpl.handleRequestVote((com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteRequest) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.VoteResponse>) responseObserver);
          break;
        case METHODID_HANDLE_APPEND_ENTRIES:
          serviceImpl.handleAppendEntries((com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesRequest) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.AppendEntriesResponse>) responseObserver);
          break;
        case METHODID_HANDLE_HEARTBEAT:
          serviceImpl.handleHeartbeat((com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.HeartbeatResponse>) responseObserver);
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

  private static abstract class ConsentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ConsentBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.fwe.flyingwhiteelephant.service.consent.raft.protocol.Raft.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Consent");
    }
  }

  private static final class ConsentFileDescriptorSupplier
      extends ConsentBaseDescriptorSupplier {
    ConsentFileDescriptorSupplier() {}
  }

  private static final class ConsentMethodDescriptorSupplier
      extends ConsentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ConsentMethodDescriptorSupplier(String methodName) {
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
      synchronized (ConsentGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ConsentFileDescriptorSupplier())
              .addMethod(getHandleRequestVoteMethod())
              .addMethod(getHandleAppendEntriesMethod())
              .addMethod(getHandleHeartbeatMethod())
              .build();
        }
      }
    }
    return result;
  }
}
