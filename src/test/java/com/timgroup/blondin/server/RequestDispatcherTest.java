package com.timgroup.blondin.server;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

public final class RequestDispatcherTest {

    private final Mockery context = new Mockery();

    private final RequestDispatcher dispatcher = new RequestDispatcher();

    @Test public void
    dispatches_404_by_default() {
        dispatcher.handle(requestFor("GET", "/"), responseExpecting(404, "Not Found"));
        context.assertIsSatisfied();
    }

    private Request requestFor(final String method, final String path) {
        final Request request = context.mock(Request.class);
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue(method));
            allowing(request).getPath(); will(returnValue(new PathParser(path)));
        }});
        
        return request;
    }
    
    private Response responseExpecting(final int statusCode, final String statusText) {
        final Response response = context.mock(Response.class);
        
        try {
            context.checking(new Expectations() {{
                oneOf(response).setCode(statusCode);
                oneOf(response).setText(statusText);
                oneOf(response).close();
            }});
        } catch (IOException e) { }
        
        return response;
    }
}
