package com.timgroup.blondin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.timgroup.blondin.proxy.BasicHttpClient;
import com.timgroup.blondin.proxy.HttpClient;
import com.timgroup.blondin.server.RequestDispatcher;

public final class BlondinServer {

    private volatile boolean available = false;
    private Connection connection;

    public BlondinServer(final int blondinPort, final String targetHost, final int targetPort) {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.register("POST", "/shutdown", new ShutdownHandler());
        dispatcher.register("GET", new ProxyingHandler(targetHost, targetPort, new BasicHttpClient()));
        
        try {
            connection = new SocketConnection(dispatcher);
            SocketAddress address = new InetSocketAddress(blondinPort);
            connection.connect(address);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        available = true;
    }

    public boolean running() {
        return available;
    }

    public void shutdown() {
        try {
            connection.close();
        }
        catch (IOException e) {
        }
        available = false;
    }
    
    private final class ShutdownHandler implements Container {
        @Override public void handle(Request request, Response response) {
            try {
                response.close();
                shutdown();
                return;
            }
            catch (Exception e) { }
        }
    }
    
    private static final class ProxyingHandler implements Container {
        private final String targetHost;
        private final int targetPort;
        private final HttpClient httpClient;
        
        public ProxyingHandler(String targetHost, int targetPort, HttpClient httpClient) {
            this.targetHost = targetHost;
            this.targetPort = targetPort;
            this.httpClient = httpClient;
        }
        
        @Override public void handle(Request request, Response response) {
            httpClient.handle(targetHost, targetPort, request, response);
        }
    }
}
