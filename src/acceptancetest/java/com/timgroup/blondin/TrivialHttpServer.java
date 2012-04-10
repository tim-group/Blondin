package com.timgroup.blondin;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public final class TrivialHttpServer implements HttpHandler {
    private final String responseString;

    private TrivialHttpServer(String responseString) {
        this.responseString = responseString;
    }

    public static HttpHandler serving(String responseString) {
        return new TrivialHttpServer(responseString);
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        response.header("Content-type", "text/plain")
                .content(responseString)
                .end();
    }
}