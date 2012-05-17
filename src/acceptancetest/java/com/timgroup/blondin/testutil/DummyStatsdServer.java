package com.timgroup.blondin.testutil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableList;

public final class DummyStatsdServer {

    private final AtomicInteger connectionsRecorded = new AtomicInteger(0);
    private final ConcurrentLinkedQueue<String> messagesReceived = new ConcurrentLinkedQueue<String>();
    private final DatagramSocket server;

    public DummyStatsdServer(int port) {
        try {
            server = new DatagramSocket(port);
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
        try {
            final DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            server.receive(packet);
            messagesReceived.add(new String(packet.getData()).trim());
            connectionsRecorded.incrementAndGet();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void shutdown() {
        server.close();
    }

    public void waitForFirstConnection() {
        while (!connected()) {
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