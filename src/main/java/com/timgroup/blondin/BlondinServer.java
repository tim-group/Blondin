package com.timgroup.blondin;

import java.util.concurrent.Future;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.timgroup.blondin.proxy.BasicHttpClient;
import com.timgroup.blondin.proxy.HttpForwardingProxyHandler;

public final class BlondinServer {

    private final WebServer server;
    
    private Supplier<Boolean> available = Suppliers.ofInstance(false);

    public BlondinServer(String targetUrl, int port) {
        server = WebServers.createWebServer(port);
        server.add(new HttpForwardingProxyHandler(targetUrl, new BasicHttpClient()));
        final Future<?> startup = server.start();
        available = new Supplier<Boolean>() {
            @Override public Boolean get() { return startup.isDone(); }
        };
    }

    public boolean running() {
        return available.get();
    }

    public void shutdown() {
        final Supplier<Boolean> underlyingAvailability = this.available;
        final Future<? extends WebServer> shutdown = server.stop();
        available = new Supplier<Boolean>() {
            @Override public Boolean get() { return shutdown.isDone() ? false : underlyingAvailability.get(); }
        };
    }
}
