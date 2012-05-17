package com.timgroup.blondin.server.handler;

import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.blondin.diagnostics.Monitor;

public final class MetricRecordingHandlerTest {

    private final Mockery context = new Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Monitor monitor = context.mock(Monitor.class);
    private final Container decoratedHandler = context.mock(Container.class);
    private final Container handler = new MetricRecordingHandler(monitor, "banana.split", decoratedHandler);

    @Test public void 
    delegates_to_decorated_handler() {
        context.checking(new Expectations() {{
            oneOf(decoratedHandler).handle(with(sameInstance(request)), with(sameInstance(response)));
            ignoring(monitor);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    plots_each_incoming_request() {
        context.checking(new Expectations() {{
            oneOf(monitor).plot("banana.split", 1);
            ignoring(decoratedHandler);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }
}
