package com.test.grpc.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.test.grpc.client.utility.TestUtil;
import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.model.response.SomeResponse;
import com.test.grpc.server.TestServer;
import com.test.grpc.service.SingleRequestServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static com.test.grpc.client.utility.TestUtil.createChannel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class TestSingleRequestClient {

    private static final TestServer server = new TestServer();

    // grpc supports 3 separate stubs/clients
    // 1. Blocking stub : blocks the execution till the response is received
    //             works for:   request = Single , response = Single
    //                          request = Single,  response = Multiple
    //
    // 2. Future stub : return a ListenableFuture object
    //            works for:    request = Single , response = Single
    //
    // 3. Stub:         Non blocking client
    //            works for:    request = Single ,   response = Single
    //                          request = Single ,   response = Multiple
    //                          request = Multiple , response = Single
    //                          request = Multiple , response = Multiple

    private final ManagedChannel channel = createChannel();

    SingleRequestServiceGrpc.SingleRequestServiceBlockingStub blockingStub =
            SingleRequestServiceGrpc.newBlockingStub(channel);

    SingleRequestServiceGrpc.SingleRequestServiceFutureStub futureStub =
            SingleRequestServiceGrpc.newFutureStub(channel);

    SingleRequestServiceGrpc.SingleRequestServiceStub stub =
            SingleRequestServiceGrpc.newStub(channel);

    @Test
    public void testBlockingStub() {

        log.info("Starting test execution :: Single request | Blocking stub");


        SomeRequest request = TestUtil.getSampleRequest("testBlockingClient");
        log.info("Request prepared {}", request);


        log.info("Calling for single request object and expecting single response");
        SomeResponse response = blockingStub.getRecord(request);
        log.info("Response received {}", response);
        assertTrue(
                response.getMetadata().getMetadata().contains("testBlockingClient"));


        log.info("Calling for single request and expecting multiple of responses");
        Iterator<SomeResponse> streamResponse = blockingStub.getStream(request);
        int[] count = {0};
        streamResponse.forEachRemaining(someResponse -> {
            count[0]++;
            log.info("Response received {}", someResponse);
            assertTrue(
                    someResponse.getMetadata().getMetadata().contains("testBlockingClient"));
        });
        assertEquals(5, count[0]);
    }

    @Test
    public void testFutureStub() throws ExecutionException, InterruptedException {

        log.info("Starting test execution :: Single request | Future stub");

        SomeRequest request = TestUtil.getSampleRequest("testNonBlockingClient");
        ListenableFuture<SomeResponse> response = futureStub.getRecord(request);
        SomeResponse someResponse = response.get();
        assertTrue(
                someResponse.getMetadata().getMetadata().contains("testNonBlockingClient"));

    }

    @Test
    public void testStub() throws InterruptedException {

        log.info("Starting test execution :: Single request | Stub");


        SomeRequest request = TestUtil.getSampleRequest("testStub");
        log.info("Request prepared {}", request);


        stub.getRecord(request, new StreamObserver<>() {
            @Override
            public void onNext(SomeResponse someResponse) {
                log.info("Response received {}", someResponse);
                Assertions.assertTrue(someResponse.getMetadata().getMetadata().contains("testStub"));
            }

            @Override
            public void onError(Throwable ex) {
                log.error("Exception while testing stub", ex);
            }

            @Override
            public void onCompleted() {
                log.info("Completed");
            }
        });

        // waiting for response
        Thread.sleep(1000L);

    }

}
