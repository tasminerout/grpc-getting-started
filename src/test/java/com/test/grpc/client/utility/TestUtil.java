package com.test.grpc.client.utility;

import com.google.protobuf.ByteString;
import com.test.grpc.model.request.SomeRequest;
import com.test.grpc.server.TestServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class TestUtil {
    private static final String GRPC_SERVER_HOST = "localhost";

    public static ManagedChannel createChannel(){
        return ManagedChannelBuilder
                .forAddress(GRPC_SERVER_HOST, TestServer.SERVER_PORT)
                .usePlaintext()
                .build();
    }

    public static SomeRequest getSampleRequest(String payload){
        return SomeRequest.newBuilder()
                .setId(32)
                .setSomeBool(true)
                .setSomeBytes(ByteString.copyFrom("bytes-test", StandardCharsets.UTF_8))
                .setSomeDouble(2D)
                .setSomeFloat(4F)
                .setSomeString(payload)
                .setEnumVal(SomeRequest.SomeEnum.ENUM_VALUE4)
                .build();
    }
}
