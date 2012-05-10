package com.timgroup.blondin.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.parse.AddressParser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.timgroup.blondin.DummyMonitor;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public final class ProxyingHandlerTest {

    private final Mockery context = new Mockery();
    
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    private HttpServer server;
    private final Container proxy = ProxyingHandler.create(new DummyMonitor(), "localhost", 30215);
    
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
        
        proxy.handle(request, response);
        
        assertThat(outputStream.toString(), is("myContent"));
    }
    
    @Test public void
    preserves_request_headers() throws Exception {
        final Map<String, List<String>> receivedHeaders = Maps.newHashMap();
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                receivedHeaders.putAll(exchange.getRequestHeaders());
                exchange.close();
            }
        });
        server.start();
        
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            allowing(request).getNames(); will(returnValue(ImmutableList.of("Accept", "Cookie", "Host", "X-Forwarded-Host", "X-Forwarded-Proto")));
            allowing(request).getValues("Accept"); will(returnValue(ImmutableList.of("text/plain")));
            allowing(request).getValues("Cookie"); will(returnValue(ImmutableList.of("$Version=1", "Skin=new")));
            allowing(request).getValues("Host"); will(returnValue(ImmutableList.of("sausage.com")));
            allowing(request).getValues("X-Forwarded-Host"); will(returnValue(ImmutableList.of("ketchup.org:8080")));
            allowing(request).getValues("X-Forwarded-Proto"); will(returnValue(ImmutableList.of("https")));
            
            ignoring(response);
        }});

        proxy.handle(request, response);
        
        assertThat(receivedHeaders.get("Accept"), Matchers.<List<String>>is(ImmutableList.of("text/plain")));
        assertThat(receivedHeaders.get("Cookie"), Matchers.<List<String>>is(ImmutableList.of("$Version=1,Skin=new")));
        assertThat(receivedHeaders.get("Host"), Matchers.<List<String>>is(ImmutableList.of("sausage.com")));
        assertThat(receivedHeaders.get("X-forwarded-host"), Matchers.<List<String>>is(ImmutableList.of("ketchup.org:8080")));
        assertThat(receivedHeaders.get("X-forwarded-proto"), Matchers.<List<String>>is(ImmutableList.of("https")));
    }
    
    @Test public void
    preserves_response_status_code() throws Exception {
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
                exchange.close();
            }
        });
        server.start();
        
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            
            oneOf(response).setCode(Status.NO_CONTENT.getCode());
            
            ignoring(request);
            ignoring(response);
        }});
        
        proxy.handle(request, response);
        
        context.assertIsSatisfied();
    }
    
    @Test public void
    preserves_response_headers() throws Exception {
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

        proxy.handle(request, response);
        
        context.assertIsSatisfied();
    }
    
    @Test public void
    preserves_request_query() throws Exception {
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
        
        proxy.handle(request, response);
        
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
        
        proxy.handle(request, response);
        
        assertThat(outputStream.toString(), is(""));
    }
    
    @Test public void
    handles_a_resource_not_found_with_content() throws Exception {
        server.createContext("/some/path/to/a/resource.txt", new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "myContent".getBytes();
                exchange.getResponseHeaders().add("Content-Type", "text/html");
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
        
        proxy.handle(request, response);
        
        assertThat(outputStream.toString(), is("myContent"));
    }

    @Test public void
    handles_a_resource_not_found_with_content_and_no_content_length_header() throws Exception {
        server.stop(0);
        final SocketLevelHttpServer socketLevelHttpServer = new SocketLevelHttpServer(30215);
        
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            allowing(request).getAddress(); will(returnValue(new AddressParser("/some/path/to/a/resource.txt")));
            
            allowing(response).getOutputStream(); will(returnValue(outputStream));
            
            ignoring(request);
            ignoring(response);
        }});
        
        proxy.handle(request, response);
        
        socketLevelHttpServer.shutdown();
        assertThat(outputStream.toString(), is("myContent"));
    }

    private static final class SocketLevelHttpServer {
        private final ServerSocket server;
        private Thread worker;

        public SocketLevelHttpServer(int port) {
            try {
                server = new ServerSocket(port);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            
            worker = new Thread(new Runnable() {
                @Override public void run() {
                    while(!server.isClosed()) {
                        handleConnections();
                    }
                }
            });
            worker.start();
        }
        
        private void handleConnections() {
            Socket connection = null;
            OutputStreamWriter outputStream = null;
            try {
                connection = server.accept();
                outputStream = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                outputStream.write("HTTP/1.1 400 Bad Request\r\n");
                outputStream.write("Content-Type: application/xml\r\n");
                outputStream.write("Connection: close\r\n");
                outputStream.write("\r\n");
                outputStream.write("myContent");
            } catch (IOException e) {
            } finally {
                Closeables.closeQuietly(outputStream);
            }
        }

        public void shutdown() {
            try {
                server.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
