package com.timgroup.blondin;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ShutdownTest {

    @Test public void
    responds_to_a_shutdown_request() throws Exception {
        new BlondinServer("localhost:80", 23454);
        TrivialHttpClient.waitForSocket("localhost", 23454);
        
        TrivialHttpClient.post("http://localhost:23454/shutdown");
        
        assertThat(TrivialHttpClient.isSocketOpen("localhost", 23454), is(false));
    }
}