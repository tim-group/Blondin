package com.timgroup.blondin.proxy;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import static org.hamcrest.Matchers.sameInstance;

public class ProxyingHandlerTest {

    private final Mockery context = new Mockery();
    
    @Test public void
    proxies_request_using_supplied_client() {
        final HttpClient httpClient = context.mock(HttpClient.class);
        final Request request = context.mock(Request.class);
        final Response response = context.mock(Response.class);
        
        context.checking(new Expectations() {{
            oneOf(httpClient).handle(with("myHost"), with(1234), with(sameInstance(request)), with(sameInstance(response)));
        }});
        
        final ProxyingHandler handler = new ProxyingHandler("myHost", 1234, httpClient);
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

}
