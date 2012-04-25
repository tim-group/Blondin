package com.timgroup.blondin.diagnostics;

import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.io.CharStreams;
import com.timgroup.blondin.DummyMonitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class GraphiteRecorderTest {

    @Test(timeout=5000L) public void
    sends_records_to_graphite() throws Exception {
        long before = System.currentTimeMillis() / 1000;
        final GraphiteRecorder recorder = new GraphiteRecorder(new DummyMonitor(), "localhost", 19253);
        recorder.record("blah", 12);
        long after = System.currentTimeMillis() / 1000;
        
        final ServerSocket server = new ServerSocket(19253);
        recorder.run();
        final Socket receiver = server.accept();
        final List<String> messagesReceived = CharStreams.readLines(new InputStreamReader(receiver.getInputStream(), "UTF-8"));
        receiver.close();
        
        assertThat(messagesReceived.size(), is(1));
        final String[] messageComponents = messagesReceived.get(0).split(" ");
        assertThat(messageComponents[0], is("blondin.blah"));
        assertThat(messageComponents[1], is("12"));
        assertThat(Long.parseLong(messageComponents[2]), is(Matchers.greaterThanOrEqualTo(before)));
        assertThat(Long.parseLong(messageComponents[2]), is(Matchers.lessThanOrEqualTo(after)));
    }

}
