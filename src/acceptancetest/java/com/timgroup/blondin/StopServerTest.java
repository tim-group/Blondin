package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.Sockets;
import com.timgroup.blondin.testutil.TrivialHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class StopServerTest extends BlondinAcceptanceTestBase {

    @Test public void
    responds_to_a_stop_request() throws Exception {
        TrivialHttpClient.post(blondinUrl() + "/stop");
        
        Sockets.waitForNoSocket("localhost", blondinPort());
        assertThat(Sockets.isSocketOpen("localhost", blondinPort()), is(false));
    }
}