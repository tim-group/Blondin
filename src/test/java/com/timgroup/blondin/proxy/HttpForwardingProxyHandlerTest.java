package com.timgroup.blondin.proxy;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import static org.hamcrest.Matchers.sameInstance;

public final class HttpForwardingProxyHandlerTest {

    private final Mockery context = new Mockery();
    
    private final HttpClient client = context.mock(HttpClient.class);
    private final HttpForwardingProxyHandler handler = new HttpForwardingProxyHandler("targetserver:2222", client);
    
    @Test public void
    forwards_request() throws Exception {
        final Request request = context.mock(Request.class);
        final Response response = context.mock(Response.class);
        
        context.checking(new Expectations() {{
            oneOf(client).handle(with("targetserver"), with(2222), with(sameInstance(request)), with(sameInstance(response)));
        }});
        
        handler.handleHttpRequest(request, response);
        context.assertIsSatisfied();
    }
}
