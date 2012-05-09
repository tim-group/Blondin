package com.timgroup.blondin.server.handler;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.handler.DefensiveHandler;

public final class DefensiveHandlerTest {

    private final Mockery context = new Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Monitor monitor = context.mock(Monitor.class);
    private final Container decoratedHandler = context.mock(Container.class);
    private final Container handler = new DefensiveHandler(monitor, decoratedHandler);

    @Test public void 
    delegates_to_decorated_handler() throws IOException {
        context.checking(new Expectations() {{
            oneOf(decoratedHandler).handle(with(sameInstance(request)), with(sameInstance(response)));
            ignoring(response);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    closes_response_after_decorated_handler_finishes() throws IOException {
        context.checking(new Expectations() {{
            final States decoratedHandling = context.states("decoratedHandling").startsAs("unhandled");
            oneOf(decoratedHandler).handle(with(any(Request.class)), with(any(Response.class))); then(decoratedHandling.is("handled"));
            oneOf(response).close(); when(decoratedHandling.is("handled"));
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    logs_exception_if_response_close_throws() throws IOException {
        context.checking(new Expectations() {{
            IOException exception = new IOException();
            oneOf(response).close(); will(throwException(exception));
            oneOf(monitor).logError(with(equalTo(DefensiveHandler.class)), 
                                    with(containsString("Failed to close response")), 
                                    with(sameInstance(exception)));
            ignoring(decoratedHandler);
        }});

        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    closes_response_even_if_decorated_handler_throws_an_exception() throws IOException {
        context.checking(new Expectations() {{
            oneOf(decoratedHandler).handle(with(any(Request.class)), with(any(Response.class))); 
                will(throwException(new RuntimeException("A problem occurred in the decorated handler")));
            oneOf(response).close();
            ignoring(monitor);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void 
    logs_exceptions_thrown_by_decorated_handler() throws IOException {
        context.checking(new Expectations() {{
            RuntimeException exception = new RuntimeException("A problem occurred in the decorated handler");
            oneOf(decoratedHandler).handle(with(any(Request.class)), with(any(Response.class))); 
                will(throwException(exception));
            oneOf(monitor).logError(with(Matchers.equalTo(DefensiveHandler.class)), 
                                    with(containsString("Failed to handle request")), 
                                    with(sameInstance(exception)));

            ignoring(response);
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

}
