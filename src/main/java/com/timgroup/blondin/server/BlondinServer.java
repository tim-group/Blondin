package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.google.common.base.Supplier;
import com.timgroup.blondin.config.ExpensiveResourceListLoader;
import com.timgroup.blondin.proxy.BasicHttpClient;
import com.timgroup.blondin.proxy.ProxyingHandler;

public final class BlondinServer {

    private final Connection connection;

    private volatile BlondinServerStatus status = BlondinServerStatus.STOPPED;

    private final Supplier<BlondinServerStatus> statusSupplier = new Supplier<BlondinServerStatus>() {
        @Override public BlondinServerStatus get() {
            return status;
        }
    };

    public BlondinServer(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl) {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.register("POST", "/stop", new StopHandler());
        dispatcher.register("POST", "/suspend", new SuspendHandler());
        dispatcher.register("GET", "/status", new StatusPageHandler(statusSupplier, new ExpensiveResourceListLoader(expensiveResourcesUrl)));
        dispatcher.register("GET", new ProxyingHandler(targetHost, targetPort, new BasicHttpClient()));
        
        try {
            connection = new SocketConnection(dispatcher);
            connection.connect(new InetSocketAddress(blondinPort));
            status = BlondinServerStatus.RUNNING;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public BlondinServerStatus status() {
        return status;
    }

    public void stop() {
        try {
            connection.close();
        }
        catch (IOException e) {
        }
        status = BlondinServerStatus.STOPPED;
    }

    public void suspend() {
        status = BlondinServerStatus.SUSPENDED;
    }

    private final class SuspendHandler implements Container {
        @Override public void handle(Request request, Response response) {
            closeSafely(response);
            suspend();
        }
    }
    
    private final class StopHandler implements Container {
        @Override public void handle(Request request, Response response) {
            closeSafely(response);
            stop();
        }
    }

    private static void closeSafely(Response response) {
        try {
            response.close();
        }
        catch (Exception e) { }
    }
}
