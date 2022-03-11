package com.test.grpc.server.service;

import com.googlecode.protobuf.format.JsonFormat;
import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.model.response.SomeResponse;
import com.test.grpc.service.SingleRequestServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;

import java.util.stream.IntStream;

import static com.test.grpc.server.util.ResponseMessageUtil.getSampleResponseMessage;

@Log4j2
public class SingleRequestServiceImpl extends SingleRequestServiceGrpc.SingleRequestServiceImplBase {

    @Override
    public void getRecord(SomeRequest request,
                          StreamObserver<SomeResponse> responseObserver) {
        log.info("Invoked single request single response");
        processMessages(1, request, responseObserver);
    }

    @Override
    public void getStream(SomeRequest request,
                          StreamObserver<SomeResponse> responseObserver) {
        log.info("Invoked single request stream response");
        processMessages(5, request, responseObserver);
    }

    private void processMessages(int noOfMessage, SomeRequest request,
                                 StreamObserver<SomeResponse> responseObserver) {
        JsonFormat format = new JsonFormat();

        log.info("Incoming message {}", format.printToString(request));
        IntStream.rangeClosed(1, noOfMessage)
                .boxed()
                .forEach(i ->
                    responseObserver.onNext(
                            getSampleResponseMessage(
                                    "Seq_" + i + "_"
                                            +request.getSomeString())));
        log.info("Marking as completed");
        responseObserver.onCompleted();
    }
}
