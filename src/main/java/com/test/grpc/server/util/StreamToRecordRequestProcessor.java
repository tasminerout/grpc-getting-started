package com.test.grpc.server.util;

import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.model.response.SomeResponse;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class StreamToRecordRequestProcessor implements StreamObserver<SomeRequest> {

    private final StreamObserver<SomeResponse> responseObserver;

    private final List<String> requestMessages = new ArrayList<>();

    @Override
    public void onNext(SomeRequest someRequest) {
        log.info("Found request {}", someRequest.getSomeString());
        requestMessages.add(someRequest.getSomeString());
    }

    @Override
    public void onError(Throwable ex) {
        log.error("Exception occurred while serving Stream request", ex);
        responseObserver.onError(ex);
    }

    @Override
    public void onCompleted() {
        String result = String.join(",", requestMessages);
        log.info("Response evaluated for the stream {}", result);
        responseObserver.onNext(ResponseMessageUtil.getSampleResponseMessage(result));
        responseObserver.onCompleted();
    }
}
