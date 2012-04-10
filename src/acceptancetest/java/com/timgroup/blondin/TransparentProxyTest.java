package com.timgroup.blondin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
        
        assertThat(contentFrom("http://localhost:" + balancer.port() + "/some/target/url"), is("hello, world"));
    }

    private String contentFrom(final String balancerUrlString) throws MalformedURLException, IOException {
        final URL url = new URL(balancerUrlString);
        final URLConnection conn = url.openConnection();
        final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
        final StringBuilder responseText = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseText.append(inputLine);
        }
        in.close();
        return inputLine;
    }
}
