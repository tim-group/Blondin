package com.timgroup.blondin.throttler;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import static org.hamcrest.Matchers.sameInstance;

public final class ThrottlingHandlerTest {

    private final Mockery context = new Mockery();

    @Test public void
    delegates_request_handling_to_decorated_handler() {
        final Request request = context.mock(Request.class);
        final Response response = context.mock(Response.class);
        final Container delegate = context.mock(Container.class);
        
        context.checking(new Expectations() {{
            oneOf(delegate).handle(with(sameInstance(request)), with(sameInstance(response)));
        }});
        
        new ThrottlingHandler(delegate, 1).handle(request, response);
        context.assertIsSatisfied();
    }

}
