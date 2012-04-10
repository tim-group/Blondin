package com.timgroup.blondin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.webbitserver.WebServers.createWebServer;

public final class TransparentProxyTest {

    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        final WebServer targetServer = createWebServer(0).add("/some/target/url", StubHandler.returning("hello, world"));
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

    public static final class StubHandler implements HttpHandler {
        private final String responseString;

        private StubHandler(String responseString) {
            this.responseString = responseString;
        }

        public static HttpHandler returning(String responseString) {
            return new StubHandler(responseString);
        }

        @Override
        public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
            response.header("Content-type", "text/plain")
                    .content(responseString)
                    .end();
        }
    }
}
