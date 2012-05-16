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
    private final HttpServer server;
    private final int port;

    private final Map<String, List<String>> requestHeaders = Maps.newHashMap();
    private final AtomicInteger numberToBlock = new AtomicInteger(0);
    private final CountDownLatch trigger = new CountDownLatch(1);
    private final AtomicInteger fulfilling = new AtomicInteger(0);
    private final AtomicInteger requestsReceived = new AtomicInteger(0);
    private final AtomicInteger requestsFulfilled = new AtomicInteger(0);

    private String lastRequestedQuery;
    private String lastRequestedUrl;

    private TrivialHttpServer(int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.start();
        Sockets.waitForSocket("localhost", port);
    }

    public TrivialHttpServer serving(String path, String content) {
        return serving(path, content, HttpURLConnection.HTTP_OK);
    }

    public TrivialHttpServer servingRedirect(String path, String content) {
        return serving(path, content, HttpURLConnection.HTTP_MOVED_TEMP);
    }

    public TrivialHttpServer serving(final String path, final String content, final int code) {
        return serving(path, content, code, true);
    }

    public TrivialHttpServer servingUnblockably(final String path, final String content, final int code) {
        return serving(path, content, code, false);
    }

    private TrivialHttpServer serving(final String path, final String content, final int code, final boolean blockable) {
        server.createContext(path, new HttpHandler() {
            @Override public void handle(final HttpExchange exchange) throws IOException {
                requestsReceived.incrementAndGet();
                new Thread(new Runnable() { @Override public void run() {
                fulfilRequest(content, code, exchange, blockable); } }).start();
            }
        });
        return this;
    }
    
    public TrivialHttpServer blockingAll() {
        return blockingFirst(Integer.MAX_VALUE);
    }

    public TrivialHttpServer blockingFirst(int requestCount) {
        this.numberToBlock.set(requestCount);
        return this;
    }
    
    public static TrivialHttpServer on(final int port) throws Exception {
        return new TrivialHttpServer(port);
    }

    private void fulfilRequest(String content, int code, final HttpExchange exchange, boolean blockable) {
        try {
            fulfilling.incrementAndGet();
            lastRequestedQuery = exchange.getRequestURI().getQuery();
            lastRequestedUrl = "http://" + exchange.getRequestHeaders().getFirst("Host") + exchange.getRequestURI();
            requestHeaders.putAll(exchange.getRequestHeaders());
            addRedirectLocationHeader("http://localhost:"+port+"/redirectionTarget", code, exchange);
            byte[] response = content.getBytes();
            exchange.sendResponseHeaders(code, response.length);
            if (fulfilling.get() <= numberToBlock.get() && blockable) {
                trigger.await();
            }
            exchange.getResponseBody().write(response);
            exchange.close();
            requestsFulfilled.incrementAndGet();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void addRedirectLocationHeader(final String location, int code, final HttpExchange exchange) {
        if (code >= 300 && code < 400) {
            exchange.getResponseHeaders().add("Location", location);
        }
    }
    
    public String query() {
        return lastRequestedQuery;
    }

    public String header(String argName) {
        return Joiner.on(",").join(requestHeaders.get(argName));
    }

    public String requestUrl() {
        return lastRequestedUrl;
    }

    public int fulfilling() {
        return this.fulfilling.get();
    }
    
    public int totalRequestsReceived() {
        return this.requestsReceived.get();
    }
    
    public int totalRequestsFulfilled() {
        return this.requestsFulfilled.get();
    }
    
    public void unblock() {
        this.trigger.countDown();
    }
}