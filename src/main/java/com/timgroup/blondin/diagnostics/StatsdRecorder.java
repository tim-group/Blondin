package com.timgroup.blondin.diagnostics;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public final class StatsdRecorder {

    private final Monitor monitor;
    private final DatagramSocket clientSocket;

    public StatsdRecorder(Monitor monitor, String host, int port) {
        this.monitor = monitor;
        
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.connect(new InetSocketAddress(host, port));
        } catch (Exception e) {
            this.monitor.logError(StatsdRecorder.class, "Failed to open socket to statsd", e);
        }
        this.clientSocket = socket;
    }

    private void send(String message) {
        try {
            final byte[] sendData = message.getBytes();
            final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            this.monitor.logWarning(StatsdRecorder.class, "Failed to write to statsd", e);
        }
    }

    public void record(String aspect, int value) {
        send(String.format("%s:%d|c", aspect, value));
    }
}
