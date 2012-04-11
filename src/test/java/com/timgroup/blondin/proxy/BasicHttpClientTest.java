package com.timgroup.blondin.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.webbitserver.stub.StubHttpRequest;
import org.webbitserver.stub.StubHttpResponse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BasicHttpClientTest {

    private HttpServer server;
    
    @Before
    public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress(30215), 0);
    }
    
    @After
    public void stopServer() throws Exception {
        server.stop(0);
    }
    
    @Test public void
    fulfils_simple_request() throws Exception {
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
        new BasicHttpClient().handle(new StubHttpRequest().uri("http://localhost:30215/some/path/to/a/resource.txt"), response);
        
        assertThat(response.contentsString(), is("myContent"));
    }
    
    @Test public void
    preseves_response_status_code() throws Exception {
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
                exchange.close();
            }
        });
        server.start();
        
        final StubHttpResponse response = new StubHttpResponse();
        new BasicHttpClient().handle(new StubHttpRequest().uri("http://localhost:30215/some/path/to/a/resource.txt"), response);
        
        assertThat(response.status(), is(HttpURLConnection.HTTP_NO_CONTENT));
    }
    
    @Test public void
    preseves_response_headers() throws Exception {
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 9);
                exchange.close();
            }
        });
        server.start();
        
        final StubHttpResponse response = new StubHttpResponse();
        new BasicHttpClient().handle(new StubHttpRequest().uri("http://localhost:30215/some/path/to/a/resource.txt"), response);
        
        assertThat(response.header("Content-length"), is("9"));
        assertThat(response.containsHeader(null), is(false));
    }
}
