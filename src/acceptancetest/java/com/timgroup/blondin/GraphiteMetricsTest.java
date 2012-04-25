package com.timgroup.blondin;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.DummyGraphiteServer;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public final class GraphiteMetricsTest extends BlondinAcceptanceTestBase {

    private final int graphitePort = generatePort();
    private final DummyGraphiteServer graphite = new DummyGraphiteServer(graphitePort);

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("graphite.host", "localhost");
        properties.setProperty("graphite.port", String.valueOf(graphitePort));
        properties.setProperty("graphite.period", "1");
        properties.setProperty("graphite.periodunit", "MILLISECONDS");
    }

    @After
    public void shutdownGraphiteServer() {
        graphite.shutdown();
    }

    @Test(timeout=5000) public void
    blondin_connects_to_graphite() throws Exception {
        graphite.waitForFirstConnection();
        
        assertThat(graphite.connected(), is(true));
    }

    @Ignore("Pending Implementation")
    @Test(timeout=5000) public void
    gathers_metrics_for_incoming_connections() throws Exception {
        TrivialHttpServer.serving("/hi", "1").on(targetPort());
        TrivialHttpClient.getFrom(blondinUrl() + "/hi");
        
        graphite.waitForNextConnection();
        
        assertThat(graphite.messagesReceived(), contains(containsString("/hi")));
    }

}
