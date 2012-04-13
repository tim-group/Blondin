package com.timgroup.blondin;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TransparentProxyTest {

    private final int blondinPort = 23453;
    private final int targetPort = 34297;
    
    @Before
    public void startBlondin() throws Exception {
        Blondin.main(new String[] {String.valueOf(blondinPort), "localhost", String.valueOf(targetPort)});
        TrivialHttpClient.waitForSocket("localhost", blondinPort);
    }
    
    @After
    public void stopBlondin() throws Exception {
        TrivialHttpClient.post(String.format("http://localhost:%s/shutdown", blondinPort));
        TrivialHttpClient.waitForNoSocket("localhost", blondinPort);
    }
    
    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort);
        
        final String requestUrl = String.format("http://localhost:%s/some/target/url", blondinPort);
        assertThat(TrivialHttpClient.contentFrom(requestUrl), is("hello, world"));
    }
}