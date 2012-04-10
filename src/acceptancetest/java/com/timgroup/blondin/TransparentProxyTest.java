package com.timgroup.blondin;

import org.junit.Test;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;

import static org.webbitserver.WebServers.createWebServer;

public final class TransparentProxyTest {

    @Test public void
    transparently_redirects_to_target_application() {
        final WebServer targetServer = createWebServer(0).add("/some/target/url", StubHandler.returning("hello, world"));
        
        // Start stub target application at x stubbing content y
        // Start blondin instance targetting x
        // Issue request to blondin for y
        // Assert response from blondin is y
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
