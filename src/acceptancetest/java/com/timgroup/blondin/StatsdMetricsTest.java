package com.timgroup.blondin;

import java.net.InetAddress;
import java.util.List;
import java.util.Properties;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.DummyStatsdServer;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

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

    @Test(timeout=5000L) public void
    gathers_metrics_for_incoming_connections() throws Exception {
        TrivialHttpServer.on(targetPort()).serving("/hi", "1");
        TrivialHttpClient.getFrom(blondinUrl() + "/hi");
        
        statsd.waitForFirstConnection();
        
        assertThat(statsd.messagesReceived().size(), is(greaterThan(0)));
        
        final String hostName = InetAddress.getLocalHost().getHostName().replace('.', '_');
        assertThat(statsd.messagesReceived(), Matchers.contains("blondin." + hostName + "." + blondinPort() + ".requests.normal:1|c"));
    }

}
