package com.test.grpc.client;

import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.model.response.SomeResponse;
import com.test.grpc.server.TestServer;
import com.test.grpc.service.StreamRequestServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static com.test.grpc.client.utility.TestUtil.createChannel;
import static com.test.grpc.client.utility.TestUtil.getSampleRequest;

@Log4j2
public class TestStreamingRequestClient {

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

    private final StreamRequestServiceGrpc.StreamRequestServiceStub stub =
            StreamRequestServiceGrpc.newStub(createChannel());


    @Test
    public void testSingleResponse() throws InterruptedException {
        StreamObserver<SomeRequest> requestMaker
                = stub.getRecordFromStream(new TestResponseObserver("testSingleResponse"));
        try {
            IntStream.rangeClosed(1, 5)
                    .boxed()
                    .forEach(i -> {
                        try {
                            Thread.sleep(400L);
                            requestMaker.onNext(getSampleRequest("testSingleResponse" + i));
                        } catch (InterruptedException e) {
                            log.error("Exception while testing streaming request", e);
                        }
                    });
        } catch (Exception ex) {
            requestMaker.onError(ex);
        } finally {
            requestMaker.onCompleted();
            Thread.sleep(1000L);
        }

    }

    @Test
    public void testStreamingResponse() throws InterruptedException {

        StreamObserver<SomeRequest> requestMaker
                = stub.getStreamFromStream(new TestResponseObserver("testStreamingResponse"));

        try {
            IntStream.rangeClosed(1, 5)
                    .boxed()
                    .forEach(i -> {
                        try {
                            Thread.sleep(400L);
                            requestMaker.onNext(getSampleRequest("testStreamingResponse" + i));
                        } catch (InterruptedException e) {
                            log.error("Exception while testing streaming request", e);
                        }
                    });
        } catch (Exception ex) {
            requestMaker.onError(ex);
        } finally {
            Thread.sleep(1000L);
            requestMaker.onCompleted();
        }
    }

    @AllArgsConstructor
    static class TestResponseObserver implements StreamObserver<SomeResponse> {

        private String message;

        @Override
        public void onNext(SomeResponse someResponse) {
            Assertions.assertTrue(someResponse.getMetadata().getMetadata().contains(message));
        }

        @Override
        public void onError(Throwable ex) {
            log.error("Exception while testing streaming request", ex);
        }

        @Override
        public void onCompleted() {
            log.info("Completed");
        }
    }

}
