package com.test.grpc.server;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class SimpleJavaRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        TestServer server = new TestServer();
        log.info("Starting server");
        server.start();
    }
}
