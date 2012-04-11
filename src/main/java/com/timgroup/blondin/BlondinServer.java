package com.timgroup.blondin;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import com.timgroup.blondin.proxy.HttpForwardingProxyHandler;

public final class BlondinServer {

    private WebServer server;

    public BlondinServer(String targetUrl) {
        this(targetUrl, 0);
    }

    public BlondinServer(String targetUrl, int port) {
        server = WebServers.createWebServer(port);
        server.add(new HttpForwardingProxyHandler("", null));
    }

    public int port() {
        return server.getPort();
    }
}
