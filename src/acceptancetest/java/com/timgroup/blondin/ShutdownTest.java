package com.timgroup.blondin;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ShutdownTest {

    @Test public void
    responds_to_a_shutdown_request() throws Exception {
        Blondin.main(new String[] {"21454", "localhost", "80"});
        TrivialHttpClient.waitForSocket("localhost", 21454);
        
        TrivialHttpClient.post("http://localhost:21454/shutdown");
        
        TrivialHttpClient.waitForNoSocket("localhost", 21454);
        assertThat(TrivialHttpClient.isSocketOpen("localhost", 21454), is(false));
    }
}