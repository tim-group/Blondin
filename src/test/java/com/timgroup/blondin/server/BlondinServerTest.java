package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import com.timgroup.blondin.DummyMonitor;

public final class BlondinServerTest {

    @Test(timeout=5000) public void
    starts_up_successfully() throws Exception {
        @SuppressWarnings("unused")
        final BlondinServer blondin = new BlondinServer(new DummyMonitor(), 21415, "x", -1, null, 1);
        waitForSocket("localhost", 21415, true);
    }
    
    @Test(timeout=5000) public void
    stops_successfully() throws Exception {
        final BlondinServer blondin = new BlondinServer(new DummyMonitor(), 21416, "x", -1, null, 1);
        waitForSocket("localhost", 21416, true);
        
        blondin.stop();
        waitForSocket("localhost", 21416, false);
    }
    
    public static void waitForSocket(String host, int port, boolean desiredState) {
        boolean currentState = !desiredState;
        while(currentState != desiredState) {
            try {
                Socket socket = new Socket(host, port);
                currentState = true;
                socket.close();
            } catch (IOException e) {
                currentState = false;
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}