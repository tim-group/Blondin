package com.timgroup.blondin.proxy;

import java.net.URL;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public final class HttpForwardingProxyHandler implements HttpHandler {

    private final String targetHost;
    private final int targetPort;
    private final ProxyClient client;

    public HttpForwardingProxyHandler(String target, ProxyClient client) {
        final String[] targetComponents = target.split(":");
        this.targetHost = targetComponents[0];
        this.targetPort = Integer.parseInt(targetComponents[1]);
        this.client = client;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        final URL requestedUrl = new URL(request.uri());
        final URL surrogateUrl = new URL(requestedUrl.getProtocol(), targetHost, targetPort, requestedUrl.getFile());
        client.handle(request.uri(surrogateUrl.toExternalForm()), response);
    }
}