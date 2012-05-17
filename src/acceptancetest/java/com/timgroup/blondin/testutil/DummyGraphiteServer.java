package com.timgroup.blondin.testutil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public final class DummyGraphiteServer {

    private final AtomicInteger connectionsRecorded = new AtomicInteger(0);
    private final ConcurrentLinkedQueue<String> messagesReceived = new ConcurrentLinkedQueue<String>();
    private final ServerSocket server;

    public DummyGraphiteServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        new Thread(new Runnable() {
            @Override public void run() {
                while(true) {
                    handleConnections();
                }
            }
        }).start();
    }
    
    private void handleConnections() {
        InputStreamReader graphiteStream = null;
        try {
            final Socket connection = server.accept();
            connectionsRecorded.incrementAndGet();
            graphiteStream = new InputStreamReader( connection.getInputStream(), "UTF-8" );
            messagesReceived.addAll(CharStreams.readLines(graphiteStream));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            Closeables.closeQuietly(graphiteStream);
        }
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
        while (connectionsRecorded.get() < target) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) { }
        }
    }

    public boolean connected() {
        return connectionsRecorded.get() > 0;
    }

    public List<String> messagesReceived() {
        return ImmutableList.copyOf(messagesReceived);
    }
}