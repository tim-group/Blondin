package com.timgroup.blondin;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ShutdownTest extends BlondinAcceptanceTestBase {

    @Test public void
    responds_to_a_shutdown_request() throws Exception {
        TrivialHttpClient.post(blondinUrl() + "/shutdown");
        
        TrivialHttpClient.waitForNoSocket("localhost", blondinPort());
        assertThat(TrivialHttpClient.isSocketOpen("localhost", blondinPort()), is(false));
    }
}