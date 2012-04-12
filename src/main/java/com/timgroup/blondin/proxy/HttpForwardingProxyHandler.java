package com.timgroup.blondin.proxy;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public final class HttpForwardingProxyHandler {

    private final String targetHost;
    private final int targetPort;
    private final HttpClient client;

    public HttpForwardingProxyHandler(String target, HttpClient client) {
        final String[] targetComponents = target.split(":");
        this.targetHost = targetComponents[0];
        this.targetPort = Integer.parseInt(targetComponents[1]);
        this.client = client;
    }

    public void handleHttpRequest(Request request, Response response) throws Exception {
        client.handle(targetHost, targetPort, request, response);
    }
}