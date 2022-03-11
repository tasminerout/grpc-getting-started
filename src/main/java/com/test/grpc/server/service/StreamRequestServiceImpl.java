package com.test.grpc.server.service;

import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.model.response.SomeResponse;
import com.test.grpc.server.util.StreamToRecordRequestProcessor;
import com.test.grpc.server.util.StreamToStreamRequestProcessor;
import com.test.grpc.service.StreamRequestServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StreamRequestServiceImpl extends StreamRequestServiceGrpc.StreamRequestServiceImplBase {

    @Override
    public StreamObserver<SomeRequest> getRecordFromStream(
            StreamObserver<SomeResponse> responseObserver) {
        return new StreamToRecordRequestProcessor(responseObserver);
    }

    @Override
    public StreamObserver<SomeRequest> getStreamFromStream(
            StreamObserver<SomeResponse> responseObserver) {
        return new StreamToStreamRequestProcessor(responseObserver);
    }

}
