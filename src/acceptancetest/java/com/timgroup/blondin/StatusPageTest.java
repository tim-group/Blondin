package com.timgroup.blondin;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

public final class StatusPageTest {

    private static int blondinPort = 22453;
    private static int targetPort = 32297;
    
    @Before
    public void startBlondin() throws Exception {
        Blondin.main(new String[] {String.valueOf(blondinPort), "localhost", String.valueOf(targetPort)});
        TrivialHttpClient.waitForSocket("localhost", blondinPort);
    }
    
    @After
    public void stopBlondin() throws Exception {
        TrivialHttpClient.post(format("http://localhost:%s/shutdown", blondinPort));
        blondinPort++;
        targetPort++;
    }
    
    @Test public void
    serves_a_status_page() throws Exception {
        final String requestUrl = format("http://localhost:%s/status", blondinPort);
        assertThat(TrivialHttpClient.contentFrom(requestUrl), startsWith("<?xml version=\"1.0\" ?>"));
    }
}