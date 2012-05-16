package com.timgroup.blondin;


import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.Sockets;
import com.timgroup.blondin.testutil.TrivialHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class StopServerTest extends BlondinAcceptanceTestBase {

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("graphite.host", "localhost");
        properties.setProperty("graphite.port", String.valueOf(generatePort()));
        properties.setProperty("graphite.period", "1");
        properties.setProperty("graphite.periodunit", "MILLISECONDS");
    }
    
    @Test(timeout=10000L) public void
    responds_to_a_stop_request() throws Exception {
        TrivialHttpClient.post(blondinUrl() + "/stop");
        
        Sockets.waitForNoSocket("localhost", blondinPort());
        assertThat(Sockets.isSocketOpen("localhost", blondinPort()), is(false));
        
        int threads = Integer.MAX_VALUE;
        while (threads > 3) {
            threads = Thread.currentThread().getThreadGroup().activeCount();
        }
    }
}