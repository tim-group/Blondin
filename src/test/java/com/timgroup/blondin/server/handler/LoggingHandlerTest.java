package com.timgroup.blondin.server.handler;

import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.parse.PathParser;

import com.timgroup.blondin.diagnostics.Monitor;

public final class LoggingHandlerTest {

    private final Mockery context = new Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Monitor monitor = context.mock(Monitor.class);
    private final Container decoratedHandler = context.mock(Container.class);
    private final Container handler = new LoggingHandler(monitor, decoratedHandler);

    @Test public void 
    delegates_to_decorated_handler() {
        context.checking(new Expectations() {{
            oneOf(decoratedHandler).handle(with(sameInstance(request)), with(sameInstance(response)));
            
            ignoring(monitor);
            ignoring(request);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    logs_each_incoming_request() {
        context.checking(new Expectations() {{
            allowing(request).getPath(); will(returnValue(new PathParser("/a/b/c")));
            oneOf(monitor).logInfo(LoggingHandler.class, "/a/b/c");
            
            ignoring(decoratedHandler);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    delegates_to_decorated_handler_even_if_logging_fails() {
        context.checking(new Expectations() {{
            allowing(monitor).logInfo(with(any(Class.class)), with(any(String.class)));
                will(throwException(new IllegalStateException()));
            
            oneOf(monitor).logError(with(LoggingHandler.class),
                                    with("Failed to log incoming request"),
                                    with(any(IllegalStateException.class)));
            oneOf(decoratedHandler).handle(with(sameInstance(request)), with(sameInstance(response)));
            
            ignoring(request);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

}
