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

public final class BlondinServer {

    private volatile boolean available = false;
    private Connection connection;

    public BlondinServer(final int blondinPort, final String targetHost, final int targetPort) {
        final BasicHttpClient httpClient = new BasicHttpClient();
        final Container container = new Container() {
            @Override
            public void handle(Request request, Response response) {
                try {
                    if ("/shutdown".equals(request.getPath().getPath()) && "POST".equals(request.getMethod())) {
                        response.close();
                        shutdown();
                        return;
                    }
                    httpClient.handle(targetHost, targetPort, request, response);
                }
                catch (Exception e) {
                }
            }
        };
        try {
            connection = new SocketConnection(container);
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
}
