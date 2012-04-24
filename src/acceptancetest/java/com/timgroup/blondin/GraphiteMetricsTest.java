package com.timgroup.blondin;

import java.net.ServerSocket;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;

public final class GraphiteMetricsTest extends BlondinAcceptanceTestBase {

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("graphite.host", "localhost");
        properties.setProperty("graphite.port", "22222");
        properties.setProperty("graphite.period", "1");
        properties.setProperty("graphite.periodunit", "MILLISECONDS");
    }

    @Test(timeout=5000) public void
    blondin_connects_to_graphite() throws Exception {
        final ServerSocket graphite = new ServerSocket(22222);
        graphite.accept();
    }
}
