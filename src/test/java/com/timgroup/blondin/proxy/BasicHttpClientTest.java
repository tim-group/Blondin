package com.timgroup.blondin.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.AddressParser;

import com.google.common.collect.Lists;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.timgroup.blondin.DummyMonitor;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public final class BasicHttpClientTest {

    private final Mockery context = new Mockery();
    
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    private HttpServer server;
    private final BasicHttpClient basicHttpClient = new BasicHttpClient(new DummyMonitor());
    
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
                byte[] responseContent = "myContent".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseContent.length);
                exchange.getResponseBody().write(responseContent);
                exchange.close();
            }
        });
        server.start();
        
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            allowing(response).getOutputStream(); will(returnValue(outputStream));

            ignoring(request);
            ignoring(response);
        }});
        
        basicHttpClient.handle("localhost", 30215, request, response);
        
        assertThat(outputStream.toString(), is("myContent"));
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
        
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            
            oneOf(response).setCode(HttpURLConnection.HTTP_NO_CONTENT);
            
            ignoring(request);
            ignoring(response);
        }});
        
        basicHttpClient.handle("localhost", 30215, request, response);
        
        context.assertIsSatisfied();
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
        
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            
            oneOf(response).add("Content-length", "9");
            never(response).add(with(nullValue(String.class)), with(any(String.class)));
            
            ignoring(request);
            ignoring(response);
        }});
        

        basicHttpClient.handle("localhost", 30215, request, response);
        
        context.assertIsSatisfied();
    }
    
    @Test public void
    preseves_request_query() throws Exception {
        final ArrayList<String> query = Lists.newArrayList();
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                query.add(exchange.getRequestURI().getQuery());
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 9);
                exchange.close();
            }
        });
        server.start();
        
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt?alpha=beta&gamma=delta")));
            
            ignoring(request);
            ignoring(response);
        }});
        
        basicHttpClient.handle("localhost", 30215, request, response);
        
        assertThat(query, is(Lists.newArrayList("alpha=beta&gamma=delta")));
    }
    
    @Test public void
    handles_a_resource_with_no_content() throws Exception {
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                exchange.close();
            }
        });
        server.start();
        
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            
            allowing(response).getOutputStream(); will(returnValue(outputStream));
            
            ignoring(request);
            ignoring(response);
        }});
        
        basicHttpClient.handle("localhost", 30215, request, response);
        
        assertThat(outputStream.toString(), is(""));
    }
    
    @Test public void
    handles_a_resource_not_found_with_content() throws Exception {
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "myContent".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        server.start();
        
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            
            allowing(response).getOutputStream(); will(returnValue(outputStream));
            
            ignoring(request);
            ignoring(response);
        }});
        
        basicHttpClient.handle("localhost", 30215, request, response);
        
        assertThat(outputStream.toString(), is("myContent"));
    }
}
