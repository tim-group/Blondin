package com.timgroup.blondin.proxy;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;


public final class ProxyingHandler implements Container {
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