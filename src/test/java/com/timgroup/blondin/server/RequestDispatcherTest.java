package com.timgroup.blondin.server;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.parse.PathParser;

import static org.hamcrest.Matchers.sameInstance;

public final class RequestDispatcherTest {

    private final Mockery context = new Mockery();

    private final RequestDispatcher dispatcher = new RequestDispatcher();

    @Test public void
    dispatches_404_by_default() {
        dispatcher.handle(requestFor("GET", "/"), responseExpecting(404, "Not Found"));
        context.assertIsSatisfied();
    }
    
    @Test public void
    dispatches_to_registered_method_handler() {
        final Container container = context.mock(Container.class);
        final Response response = context.mock(Response.class);
        final Request request = requestFor("GET", "/");
        
        dispatcher.register("GET", container);
        
        context.checking(new Expectations() {{
            oneOf(container).handle(with(sameInstance(request)), with(sameInstance(response)));
        }});
        
        dispatcher.handle(request, response);
        
        context.assertIsSatisfied();
    }
    
    @Test public void
    ignores_irrelevant_registered_method_handler() {
        final Container container = context.mock(Container.class);
        dispatcher.register("GET", container);
        
        context.checking(new Expectations() {{
            never(container);
        }});
        
        dispatcher.handle(requestFor("POST", "/"), responseExpecting(404, "Not Found"));
        
        context.assertIsSatisfied();
    }

    @Test public void
    dispatches_to_registered_path_handler() {
        final Container container = context.mock(Container.class);
        final Response response = context.mock(Response.class);
        final Request request = requestFor("POST", "/stop");
        
        dispatcher.register("POST", "/stop", container);
        
        context.checking(new Expectations() {{
            oneOf(container).handle(with(sameInstance(request)), with(sameInstance(response)));
        }});
        
        dispatcher.handle(request, response);
        
        context.assertIsSatisfied();
    }
    
    @Test public void
    ignores_irrelevant_registered_path_handler() {
        final Container container = context.mock(Container.class);
        
        dispatcher.register("POST", "/stop2", container);
        
        context.checking(new Expectations() {{
            never(container);
        }});
        
        dispatcher.handle(requestFor("POST", "/stop"), responseExpecting(404, "Not Found"));
        
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
