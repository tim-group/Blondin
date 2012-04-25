package com.timgroup.blondin.diagnostics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public final class GraphiteRecorder implements Runnable {

    private final Monitor monitor;
    private final String host;
    private final int port;

    public GraphiteRecorder(Monitor monitor, String host, int port) {
        this.monitor = monitor;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            report(socket);
        } catch (IOException e) {
            monitor.logWarning(ExternalRecorder.class, "Failed to write metrics to graphite");
        } finally {
            closeQuietly(socket);
        }
    }

    private void report(Socket socket) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //final long epoch = System.currentTimeMillis() / 1000;
        writer.flush();
    }

    private void closeQuietly(Socket socket) {
        if (null != socket) {
            try { socket.close(); } catch (IOException e) { }
        }
    }

    public void record(String aspect, Integer value) {
    }
}
