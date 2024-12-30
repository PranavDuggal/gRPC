package com.grpcautomation.tests;

import com.grpcautomation.UserRequest;
import com.grpcautomation.UserServiceGrpc;
import com.grpcautomation.client.GrpcClient;
import com.grpcautomation.UserResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class GrpcTest {

    private Server server;
    private ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    @BeforeClass
    public void startServer() throws InterruptedException {
        // Start gRPC server
        server = ServerBuilder.forPort(50051)
                .addService(new UserServiceImpl())  // Add the service to the server
                .build();

        // Start the server in a separate thread to avoid blocking the test
        new Thread(() -> {
            try {
                server.start();
                System.out.println("Server started on port 50051");
                server.awaitTermination();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Wait a few seconds to ensure the server has started
        Thread.sleep(2000);  // Adjust the sleep duration if needed
    }

    @Test
    public void testGrpcClient() {
        // Create a channel to the server
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()  // Disable SSL for simplicity
                .build();

        // Create the blocking stub for synchronous communication
        blockingStub = UserServiceGrpc.newBlockingStub(channel);

        // Create a request for the client
        String userId = "1234";
        UserRequest request = UserRequest.newBuilder().setUserId(userId).build();

        // Call the server method and get the response
        UserResponse response = blockingStub.getUserById(request);

        // Assertions to check the response from the server
        assertEquals(response.getUserId(), "1234");
        assertEquals(response.getName(), "John Doe");
        assertEquals(response.getEmail(), "john.doe@example.com");
    }

    @AfterClass
    public void shutdown() throws InterruptedException {
        // Shut down the channel and server after tests
        if (channel != null) {
            channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        }

        if (server != null) {
            server.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    // Implement the server-side logic (mock implementation)
    static class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
        @Override
        public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver) {
            // Mock user response
            UserResponse response = UserResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setName("John Doe")
                    .setEmail("john.doe@example.com")
                    .build();

            // Send the response back to the client
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
