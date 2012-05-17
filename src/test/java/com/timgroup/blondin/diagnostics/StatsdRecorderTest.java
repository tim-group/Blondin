package com.timgroup.blondin.diagnostics;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.timgroup.blondin.DummyMonitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class StatsdRecorderTest {

    private final StatsdRecorder recorder = new StatsdRecorder(new DummyMonitor(), "my.prefix", "localhost", 19254);

    @After
    public void stop() {
        recorder.stop();
    }

    @Test(timeout=5000L) public void
    sends_records_to_statsd() throws Exception {
        final List<String> messagesReceived = Lists.newArrayList();
        final DatagramSocket server = new DatagramSocket(19254);
        
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                    server.receive(packet);
                    messagesReceived.add(new String(packet.getData()).trim());
                    server.close();
                } catch (Exception e) { }
            }
        }).start();
        
        recorder.record("blah", 12);
        while (messagesReceived.isEmpty()) { Thread.sleep(50L); }
        
        assertThat(messagesReceived, contains("my.prefix.blah:12|c"));
    }

}
