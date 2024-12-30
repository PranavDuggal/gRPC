package com.grpcautomation.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.grpcautomation.UserRequest;
import com.grpcautomation.UserResponse;
import com.grpcautomation.UserServiceGrpc;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        // Create and start the server
        Server server = ServerBuilder.forPort(50051)
                .addService(new UserServiceImpl())  // Add service implementation
                .build();

        System.out.println("Server started, listening on port 50051");
        server.start();  // Start the server

        // Await termination
        server.awaitTermination();
    }

    static class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
        @Override
        public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver) {
            String userId = request.getUserId();

            // Business logic for fetching user details
            UserResponse response = UserResponse.newBuilder()
                    .setUserId(userId)
                    .setName("John Doe")
                    .setEmail("john.doe@example.com")
                    .build();

            // Send the response back to the client
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
