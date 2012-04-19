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
    private AtomicInteger numberToBlock = new AtomicInteger(0);
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
        this.numberToBlock = new AtomicInteger(requestCount);
        this.trigger = trigger;
        return this;
    }
    
    public TrivialHttpServer on(final int port) throws Exception {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new HttpHandler() {
            @Override public void handle(final HttpExchange exchange) throws IOException {
                new Thread(new Runnable() { @Override public void run() { fulfilRequest(port, server, exchange); } }).start();
            }
        });
        server.start();
        return this;
    }
    
    private void fulfilRequest(final int port, final HttpServer server, final HttpExchange exchange) {
        try {
            fulfilling.incrementAndGet();
            query = exchange.getRequestURI().getQuery();
            requestHeaders.putAll(exchange.getRequestHeaders());
            addRedirectLocationHeader("http://localhost:"+port+"/redirectionTarget", exchange);
            byte[] response = content.getBytes();
            exchange.sendResponseHeaders(code, response.length);
            if (fulfilling.get() <= numberToBlock.get()) {
                trigger.await();
            }
            exchange.getResponseBody().write(response);
            exchange.close();
            
            if (fulfilling.decrementAndGet() == 0) {
                server.stop(0);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void addRedirectLocationHeader(final String location, final HttpExchange exchange) {
        if (code >= 300 && code < 400) {
            exchange.getResponseHeaders().add("Location", location);
        }
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