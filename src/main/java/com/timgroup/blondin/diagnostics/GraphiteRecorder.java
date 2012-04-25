package com.timgroup.blondin.diagnostics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;

public final class GraphiteRecorder implements Runnable {

    private final Monitor monitor;
    private final String host;
    private final int port;

    private final LinkedBlockingQueue<Record> records = new LinkedBlockingQueue<Record>();
    
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
        final List<Record> payload = Lists.newArrayList();
        records.drainTo(payload);
        for (Record record : payload) {
            writer.append(record.asGraphiteMessage());
        }
        writer.flush();
    }

    private void closeQuietly(Socket socket) {
        if (null != socket) {
            try { socket.close(); } catch (IOException e) { }
        }
    }

    public void record(String aspect, int value) {
        final long epoch = System.currentTimeMillis() / 1000;
        records.offer(new Record(aspect, value, epoch));
    }
    
    private static final class Record {
        private final String aspect;
        private final int value;
        private final long epoch;

        public Record(String aspect, int value, long epoch) {
            this.aspect = aspect.replace(' ', '_');
            this.value = value;
            this.epoch = epoch;
        }

        public String asGraphiteMessage() {
            return String.format("blondin.%s %s %s\n", aspect, value, epoch);
        }
    }
}
