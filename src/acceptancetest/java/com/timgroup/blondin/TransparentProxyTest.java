package com.timgroup.blondin;


import org.junit.Test;
import org.webbitserver.WebServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.webbitserver.WebServers.createWebServer;

public final class TransparentProxyTest {

    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        final WebServer targetServer = createWebServer(0).add("/some/target/url", TrivialHttpServer.serving("hello, world"));
        
        final BlondinServer balancer = new BlondinServer("localhost:" + targetServer.getPort());
        
        assertThat(TrivialHttpClient.contentFrom("http://localhost:" + balancer.port() + "/some/target/url"), is("hello, world"));
    }
}