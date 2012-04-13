package com.timgroup.blondin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class TrivialHttpServer {
    private final String content;
    private final String path;
    
    private String query;

    private TrivialHttpServer(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public static TrivialHttpServer serving(String path, String content) {
        return new TrivialHttpServer(path, content);
    }
    
    public TrivialHttpServer on(int port) throws Exception {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new HttpHandler() {

            @Override public void handle(HttpExchange exchange) throws IOException {
                query = exchange.getRequestURI().getQuery();
                byte[] response = content.getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
                server.stop(0);
            }
        });
        server.start();
        return this;
    }
    
    public String query() {
        return query;
    }
}