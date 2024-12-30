package com.grpcautomation.client;

import com.grpcautomation.UserServiceGrpc;
import com.grpcautomation.UserRequest;
import com.grpcautomation.UserResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    private ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public GrpcClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()  // Disable SSL for simplicity
                .build();
        blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public UserResponse getUserById(String userId) {
        UserRequest request = UserRequest.newBuilder()
                .setUserId(userId)
                .build();
        return blockingStub.getUserById(request);
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}
