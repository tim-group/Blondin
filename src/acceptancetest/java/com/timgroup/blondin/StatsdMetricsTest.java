package com.timgroup.blondin;

import java.util.List;
import java.util.Properties;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.DummyStatsdServer;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public final class StatsdMetricsTest extends BlondinAcceptanceTestBase {

    private final int statsdPort = generatePort();
    private final DummyStatsdServer statsd = new DummyStatsdServer(statsdPort);

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("statsd.host", "localhost");
        properties.setProperty("statsd.port", String.valueOf(statsdPort));
    }

    @After
    public void shutdownGraphiteServer() {
        statsd.shutdown();
    }

    @Ignore("pending implementation")
    @Test(timeout=5000) public void
    gathers_metrics_for_incoming_connections() throws Exception {
        TrivialHttpServer.on(targetPort()).serving("/hi", "1");
        TrivialHttpClient.getFrom(blondinUrl() + "/hi");
        
        statsd.waitForNextConnection();
        
        assertThat(statsd.messagesReceived().size(), is(greaterThan(0)));
        assertThat(statsd.messagesReceived(), Matchers.<String>hasItem(startsWith("stats_counts.blondin.connections.received:1|c")));
    }

}
