package com.timgroup.blondin;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

public final class BlondinServer {

    private WebServer server;

    public BlondinServer(String targetUrl) {
        this(targetUrl, 0);
    }

    public BlondinServer(String targetUrl, int port) {
        server = WebServers.createWebServer(port);
    }

    public int port() {
        return server.getPort();
    }
}
