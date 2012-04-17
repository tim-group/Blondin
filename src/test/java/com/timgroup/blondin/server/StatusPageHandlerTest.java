package com.timgroup.blondin.server;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.google.common.base.Supplier;

import static com.timgroup.blondin.server.BlondinServerStatus.RUNNING;
import static com.timgroup.blondin.server.BlondinServerStatus.SUSPENDED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public final class StatusPageHandlerTest {

    private final Mockery context = new Mockery();
    
    @SuppressWarnings("unchecked")
    private final Supplier<BlondinServerStatus> statusSupplier = context.mock(Supplier.class);
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    private final StatusPageHandler handler = new StatusPageHandler(statusSupplier);
    
    @Test public void
    writes_status_page_to_response() throws Exception {
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/xml+status");
            oneOf(response).close();
            
            allowing(statusSupplier).get(); will(returnValue(RUNNING));
            allowing(response).getOutputStream(); will(returnValue(outputStream));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        assertThat(outputStream.toString(), startsWith("<?xml"));
    }
    
    @Test public void
    includes_version_information_in_status_page() throws Exception {
        final String currentVersion = StatusPageHandler.class.getPackage().getImplementationVersion();
        
        final OutputStream outputStream = new ByteArrayOutputStream();
        context.checking(new Expectations() {{
            allowing(response).getOutputStream(); will(returnValue(outputStream));
            
            ignoring(statusSupplier).get(); will(returnValue(RUNNING));
            ignoring(response);
        }});
        
        handler.handle(request, response);
        
        assertThat(outputStream.toString(), containsString("Version: <value>" + currentVersion + "</value>"));
    }
    
    @Test public void
    responds_with_http_status_code_of_503_if_status_is_suspended() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).setCode(503);
            oneOf(response).setText("Service Unavailable");
            oneOf(response).close();
            
            allowing(statusSupplier).get(); will(returnValue(SUSPENDED));
            allowing(response).getOutputStream(); will(returnValue(new ByteArrayOutputStream()));
            ignoring(response);
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
    }
}
