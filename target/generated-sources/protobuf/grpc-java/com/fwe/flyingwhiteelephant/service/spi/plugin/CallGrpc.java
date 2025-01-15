package com.fwe.flyingwhiteelephant.service.spi.plugin;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.1)",
    comments = "Source: plugin.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CallGrpc {

  private CallGrpc() {}

  public static final String SERVICE_NAME = "Call";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest,
      com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse> getExecuteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "execute",
      requestType = com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest.class,
      responseType = com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest,
      com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse> getExecuteMethod() {
    io.grpc.MethodDescriptor<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest, com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse> getExecuteMethod;
    if ((getExecuteMethod = CallGrpc.getExecuteMethod) == null) {
      synchronized (CallGrpc.class) {
        if ((getExecuteMethod = CallGrpc.getExecuteMethod) == null) {
          CallGrpc.getExecuteMethod = getExecuteMethod =
              io.grpc.MethodDescriptor.<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest, com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "execute"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CallMethodDescriptorSupplier("execute"))
              .build();
        }
      }
    }
    return getExecuteMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CallStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CallStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CallStub>() {
        @java.lang.Override
        public CallStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CallStub(channel, callOptions);
        }
      };
    return CallStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CallBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CallBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CallBlockingStub>() {
        @java.lang.Override
        public CallBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CallBlockingStub(channel, callOptions);
        }
      };
    return CallBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CallFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CallFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CallFutureStub>() {
        @java.lang.Override
        public CallFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CallFutureStub(channel, callOptions);
        }
      };
    return CallFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class CallImplBase implements io.grpc.BindableService {

    /**
     */
    public void execute(com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExecuteMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest,
                com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse>(
                  this, METHODID_EXECUTE)))
          .build();
    }
  }

  /**
   */
  public static final class CallStub extends io.grpc.stub.AbstractAsyncStub<CallStub> {
    private CallStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CallStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CallStub(channel, callOptions);
    }

    /**
     */
    public void execute(com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest request,
        io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CallBlockingStub extends io.grpc.stub.AbstractBlockingStub<CallBlockingStub> {
    private CallBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CallBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CallBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse execute(com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CallFutureStub extends io.grpc.stub.AbstractFutureStub<CallFutureStub> {
    private CallFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CallFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CallFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse> execute(
        com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CallImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CallImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE:
          serviceImpl.execute((com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCRequest) request,
              (io.grpc.stub.StreamObserver<com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.CCResponse>) responseObserver);
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

  private static abstract class CallBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CallBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.fwe.flyingwhiteelephant.service.spi.plugin.Plugin.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Call");
    }
  }

  private static final class CallFileDescriptorSupplier
      extends CallBaseDescriptorSupplier {
    CallFileDescriptorSupplier() {}
  }

  private static final class CallMethodDescriptorSupplier
      extends CallBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CallMethodDescriptorSupplier(String methodName) {
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
      synchronized (CallGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CallFileDescriptorSupplier())
              .addMethod(getExecuteMethod())
              .build();
        }
      }
    }
    return result;
  }
}
