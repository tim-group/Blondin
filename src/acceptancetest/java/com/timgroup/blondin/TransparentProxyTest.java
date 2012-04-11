package com.timgroup.blondin;


import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.webbitserver.WebServers.createWebServer;

public final class TransparentProxyTest {

    @Ignore("Pending Implementation")
    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        createWebServer(34297).add("/some/target/url", TrivialHttpServer.serving("hello, world")).start();
        
        new BlondinServer("localhost:34297", 23453);
        
        assertThat(TrivialHttpClient.contentFrom("http://localhost:23453/some/target/url"), is("hello, world"));
    }
}