package com.timgroup.blondin.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import org.junit.Test;
import org.webbitserver.stub.StubHttpRequest;
import org.webbitserver.stub.StubHttpResponse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BasicProxyClientTest {

    @Test public void
    fulfils_simple_request() throws Exception {
        final HttpServer server = HttpServer.create(new InetSocketAddress(30215), 0);
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "myContent".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        server.start();
        
        final StubHttpResponse response = new StubHttpResponse();
        new BasicProxyClient().handle(new StubHttpRequest().uri("http://localhost:30215/some/path/to/a/resource.txt"), response);
        server.stop(0);
        
        assertThat(response.contentsString(), is("myContent"));
    }
}
