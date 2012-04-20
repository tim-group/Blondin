package com.timgroup.blondin.throttler;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

public final class ThrottlingHandler implements Container {

    private final Container handler;

    public ThrottlingHandler(Container handler, int bandwidth) {
        this.handler = handler;
    }
    
    @Override
    public void handle(Request request, Response response) {
        handler.handle(request, response);
    }

}
