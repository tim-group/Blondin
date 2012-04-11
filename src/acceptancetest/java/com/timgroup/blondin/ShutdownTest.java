package com.timgroup.blondin;


import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ShutdownTest {

    @Ignore("pending implementation")
    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        new BlondinServer("localhost:80", 23454);
        TrivialHttpClient.waitForSocket("localhost", 23454);
        
        TrivialHttpClient.post("http://localhost:23454/shutdown");
        
        assertThat(TrivialHttpClient.isSocketOpen("localhost", 23454), is(false));
    }
}