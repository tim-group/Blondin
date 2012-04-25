package com.timgroup.blondin.testutil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class DummyGraphiteServer {

    private final AtomicInteger connectionsRecorded = new AtomicInteger(0);
    private final ServerSocket server;

    public DummyGraphiteServer(int port) {
        try {
            server = new ServerSocket(22222);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    server.accept();
                    connectionsRecorded.incrementAndGet();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

        }).start();
    }

    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void waitForFirstConnection() {
        while (!connected()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) { }
        }
    }

    public void waitForNextConnection() {
        int target = connectionsRecorded.get() + 1;
        while (connectionsRecorded.get() <= target) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) { }
        }
    }

    public boolean connected() {
        return connectionsRecorded.get() > 0;
    }

    public List<String> messagesReceived() {
        return null;
    }
}