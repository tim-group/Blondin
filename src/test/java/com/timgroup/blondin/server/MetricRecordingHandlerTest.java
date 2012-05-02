package com.timgroup.blondin.server;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.parse.PathParser;

import com.timgroup.blondin.diagnostics.Monitor;

import static org.hamcrest.Matchers.sameInstance;

public final class MetricRecordingHandlerTest {

    private final Mockery context = new Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Monitor monitor = context.mock(Monitor.class);
    private final Container decoratedHandler = context.mock(Container.class);
    private final Container handler = new MetricRecordingHandler(monitor, decoratedHandler);

    @Test public void 
    delegates_to_decorated_handler() throws IOException {
        context.checking(new Expectations() {{
            oneOf(decoratedHandler).handle(with(sameInstance(request)), with(sameInstance(response)));
            
            ignoring(monitor);
            ignoring(request);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    plots_each_incoming_request() throws IOException {
        context.checking(new Expectations() {{
            oneOf(monitor).plot("connections.received", 1);
            
            ignoring(monitor).logInfo(with(any(Class.class)), with(any(String.class)));
            ignoring(request);
            ignoring(decoratedHandler);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }
    
    @Test public void 
    logs_each_incoming_request() throws IOException {
        context.checking(new Expectations() {{
            allowing(request).getPath(); will(returnValue(new PathParser("/a/b/c")));
            oneOf(monitor).logInfo(MetricRecordingHandler.class, "/a/b/c");
            
            ignoring(monitor).plot(with(any(String.class)), with(any(Integer.class)));
            ignoring(decoratedHandler);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }
}
