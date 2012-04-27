package com.timgroup.blondin.testutil;

import java.io.IOException;
import java.net.Socket;

public class Sockets {

    public static void waitForSocket(String host, int port) throws IOException {
        Sockets.waitForSocket(host, port, true);
    }

    public static void waitForNoSocket(String host, int port) throws IOException {
        Sockets.waitForSocket(host, port, false);
    }

    public static void waitForSocket(String host, int port, boolean desiredState) throws IOException {
        long startTime = System.currentTimeMillis();
        boolean currentState = !desiredState;
        while(currentState != desiredState) {
            try {
                Socket socket = new Socket(host, port);
                currentState = true;
                socket.close();
            } catch (IOException e) {
                currentState = false;
            }
            if (System.currentTimeMillis() - startTime > 10000L) {
                throw new IllegalStateException("socket did not " + (desiredState ? "open" : "close"));
            }
        }
    }

    public static boolean isSocketOpen(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
