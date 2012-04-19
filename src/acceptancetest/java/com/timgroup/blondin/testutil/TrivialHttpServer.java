package com.timgroup.blondin.testutil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class TrivialHttpServer {
    private final String content;
    private final String path;
    
    private final int code;
    
    private String query;
    private Map<String, List<String>> requestHeaders = Maps.newHashMap();
    private CountDownLatch trigger = new CountDownLatch(0);
    private AtomicInteger requestCount = new AtomicInteger(0);
    private AtomicInteger fulfilling = new AtomicInteger(0);

    private TrivialHttpServer(String path, String content, int code) {
        this.path = path;
        this.content = content;
        this.code = code;
    }

    public static TrivialHttpServer serving(String path, String content) {
        return serving(path, content, HttpURLConnection.HTTP_OK);
    }
    
    public static TrivialHttpServer servingRedirect(String path, String content) {
        return serving(path, content, HttpURLConnection.HTTP_MOVED_TEMP);
    }
    
    public static TrivialHttpServer serving(String path, String content, int code) {
        return new TrivialHttpServer(path, content, code);
    }
    
    public TrivialHttpServer blockingFirst(int requestCount, CountDownLatch trigger) {
        this.requestCount = new AtomicInteger(requestCount);
        this.trigger  = trigger;
        return this;
    }
    
    public TrivialHttpServer on(final int port) throws Exception {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext(path, new HttpHandler() {
            @Override public void handle(HttpExchange exchange) throws IOException {
                fulfilling.incrementAndGet();
                query = exchange.getRequestURI().getQuery();
                requestHeaders.putAll(exchange.getRequestHeaders());
                if (code >= 300 && code < 400) {
                    exchange.getResponseHeaders().add("Location", "http://localhost:"+port+"/redirectionTarget");
                }
                byte[] response = content.getBytes();
                exchange.sendResponseHeaders(code, response.length);
             
                if (fulfilling.get() <= requestCount.get()) {
                    try {
                        trigger.await();
                    }
                    catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
                
                exchange.getResponseBody().write(response);
                exchange.close();
                int active = fulfilling.decrementAndGet();
                
                if (active == 0) {
                    server.stop(0);
                }
            }
        });
        server.start();
        return this;
    }
    
    public String query() {
        return query;
    }

    public String header(String argName) {
        return Joiner.on(",").join(requestHeaders.get(argName));
    }

    public int fulfilling() {
        return this.fulfilling.get();
    }
}