package com.test.grpc.server.util;

import com.test.grpc.model.response.ResponseMetadata;
import com.test.grpc.model.response.ResponsePayload;
import com.test.grpc.model.response.SomeResponse;

public class ResponseMessageUtil {

    public static SomeResponse getSampleResponseMessage(String requestPayload){
        return SomeResponse.newBuilder()
                .setMetadata(ResponseMetadata.newBuilder()
                        .setMetadata("for request payload = " + requestPayload)
                        .build())
                .putPayload("Payload1", ResponsePayload.newBuilder()
                        .addItem("Item1_1")
                        .addItem("item1_2")
                        .build())
                .putPayload("Payload2", ResponsePayload.newBuilder().build())
                .build();
    }

}
