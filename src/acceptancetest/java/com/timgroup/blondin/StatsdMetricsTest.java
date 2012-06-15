package com.timgroup.blondin;

import java.net.InetAddress;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.DummyStatsdServer;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public final class StatsdMetricsTest extends BlondinAcceptanceTestBase {

    private final int statsdPort = generatePort();
    private final DummyStatsdServer statsd = new DummyStatsdServer(statsdPort);

    private String prefix;

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("statsd.host", "localhost");
        properties.setProperty("statsd.port", String.valueOf(statsdPort));
        
        final int expensiveUrlsPort = generatePort();
        properties.setProperty("expensiveResourcesUrl", "http://localhost:" + expensiveUrlsPort + "/my/expensive/resources");
        TrivialHttpServer.on(expensiveUrlsPort).serving("/my/expensive/resources", "/low");
    }

    @Before
    public void determinePrefix() throws Exception {
        final String hostName = InetAddress.getLocalHost().getHostName().replace('.', '_');
        prefix = "blondin." + hostName + "." + blondinPort() + ".";
    }

    @After
    public void shutdownStatsDServer() {
        statsd.shutdown();
    }

    @Test(timeout=5000L) public void
    gathers_metrics_for_incoming_normal_requests() throws Exception {
        TrivialHttpServer.on(targetPort()).serving("/hi", "1");
        TrivialHttpClient.getFrom(blondinUrl() + "/hi");
        
        statsd.waitForFirstConnection();
        
        assertThat(statsd.messagesReceived().size(), is(greaterThan(0)));
        assertThat(statsd.messagesReceived(), contains(prefix + "requests.normal:1|c"));
    }

    @Test(timeout=5000L) public void
    gathers_metrics_for_incoming_expensive_requests() throws Exception {
        TrivialHttpServer.on(targetPort()).serving("/low", "1");
        TrivialHttpClient.getFrom(blondinUrl() + "/low");
        
        statsd.waitForFirstConnection();
        
        assertThat(statsd.messagesReceived().size(), is(greaterThan(0)));
        assertThat(statsd.messagesReceived(), contains(prefix + "requests.expensive:1|c"));
    }

}
