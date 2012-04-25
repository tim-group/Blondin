package com.timgroup.blondin;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.DummyGraphiteServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class GraphiteMetricsTest extends BlondinAcceptanceTestBase {

    final DummyGraphiteServer graphite = new DummyGraphiteServer(22222);

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("graphite.host", "localhost");
        properties.setProperty("graphite.port", "22222");
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
}
