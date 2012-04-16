package com.timgroup.blondin.server;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import static org.junit.Assert.assertThat;

public final class StatusPageHandlerTest {

    private final Mockery context = new Mockery();
    
    @Test public void
    writes_status_page_to_response() throws Exception {
        final StatusPageHandler handler = new StatusPageHandler();
        final Request request = context.mock(Request.class);
        final Response response = context.mock(Response.class);
        
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/xml+status");
            oneOf(response).close();
            
            allowing(response).getOutputStream(); will(returnValue(outputStream));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        assertThat(outputStream.toString(), Matchers.startsWith("<?xml"));
    }

}
