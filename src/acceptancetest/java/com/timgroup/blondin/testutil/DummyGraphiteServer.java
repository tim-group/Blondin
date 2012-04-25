package com.timgroup.blondin.testutil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DummyGraphiteServer {

    private final AtomicBoolean connected = new AtomicBoolean(false);
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
                    connected.set(true);
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

    public boolean connected() {
        return connected.get();
    }
}