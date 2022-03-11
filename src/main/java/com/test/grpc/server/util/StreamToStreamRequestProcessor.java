package com.test.grpc.server.util;

import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.model.response.SomeResponse;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.stream.IntStream;

import static com.test.grpc.server.util.ResponseMessageUtil.getSampleResponseMessage;

@AllArgsConstructor
@Log4j2
public class StreamToStreamRequestProcessor implements StreamObserver<SomeRequest> {
    private final StreamObserver<SomeResponse> responseObserver;

    @Override
    public void onNext(SomeRequest someRequest) {
        IntStream.rangeClosed(1, 5)
                .boxed()
                .forEach(val -> responseObserver.onNext(
                        getSampleResponseMessage(
                                "Iteration: "
                                        + val
                                        + " "
                                        + someRequest.getSomeString())));

    }

    @Override
    public void onError(Throwable ex) {
        log.error("Exception occurred while serving Stream request", ex);
        responseObserver.onError(ex);
    }

    @Override
    public void onCompleted() {
        log.info("On completed");
        responseObserver.onCompleted();
    }
}
