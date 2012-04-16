package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.timgroup.blondin.proxy.BasicHttpClient;
import com.timgroup.blondin.proxy.ProxyingHandler;

public final class BlondinServer {

    private final Connection connection;
    
    private volatile BlondinServerStatus status = BlondinServerStatus.STOPPED;

    public BlondinServer(final int blondinPort, final String targetHost, final int targetPort) {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.register("POST", "/stop", new StopHandler());
        dispatcher.register("GET", "/status", new StatusPageHandler());
        dispatcher.register("GET", new ProxyingHandler(targetHost, targetPort, new BasicHttpClient()));
        
        try {
            connection = new SocketConnection(dispatcher);
            SocketAddress address = new InetSocketAddress(blondinPort);
            connection.connect(address);
            status = BlondinServerStatus.RUNNING;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean running() {
        return BlondinServerStatus.RUNNING.equals(status);
    }

    public void stop() {
        try {
            connection.close();
        }
        catch (IOException e) {
        }
        status = BlondinServerStatus.STOPPED;
    }
    
    private final class StopHandler implements Container {
        @Override public void handle(Request request, Response response) {
            try {
                response.close();
                stop();
                return;
            }
            catch (Exception e) { }
        }
    }
}
