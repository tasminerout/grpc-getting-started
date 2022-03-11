package com.test.grpc.server;

import com.test.grpc.server.service.SingleRequestServiceImpl;
import com.test.grpc.server.service.StreamRequestServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Objects;

@Log4j2
public class TestServer {

    public static final int SERVER_PORT = 10880;

    private Server server;

    public void start() throws InterruptedException, IOException {
        server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(new SingleRequestServiceImpl())
                .addService(new StreamRequestServiceImpl())
                .build();
        server.start();
        log.info("Server started on port " + SERVER_PORT);
        server.awaitTermination();
    }

    public void stop(){
        if(Objects.nonNull(server) && !server.isTerminated()){
            server.shutdownNow();
        }
    }
}
