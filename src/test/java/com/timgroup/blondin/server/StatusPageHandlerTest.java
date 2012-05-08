package com.timgroup.blondin.server;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.timgroup.blondin.DummyMonitor;

import static com.timgroup.blondin.server.BlondinServerStatus.RUNNING;
import static com.timgroup.blondin.server.BlondinServerStatus.SUSPENDED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public final class StatusPageHandlerTest {

    private final Mockery context = new Mockery();
    
    @SuppressWarnings("unchecked")
    private final Supplier<BlondinServerStatus> statusSupplier = context.mock(Supplier.class);
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    private final AtomicBoolean outputStreamClosed = new AtomicBoolean(false);
    private final OutputStream responseContent = new ByteArrayOutputStream() {
        public void close() throws java.io.IOException { outputStreamClosed.set(true); super.close(); };
    };

    private final List<String> blackList = Lists.newArrayList();
    private final StatusPageHandler handler = new StatusPageHandler(new DummyMonitor(), statusSupplier, Suppliers.<Iterable<String>>ofInstance(blackList));
    
    @Before
    public void attachResponseContent() throws Exception {
        context.checking(new Expectations() {{
            allowing(response).getOutputStream(); will(returnValue(responseContent));
        }});
    }
    
    @Test public void
    responds_to_request_for_version() throws Exception {
        final String expectedVersion = Strings.nullToEmpty(StatusPageHandler.class.getPackage().getImplementationVersion());
        
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/plain");
            oneOf(response).add("Content-Type", "charset=UTF-8");
            
            allowing(request).getPath(); will(returnValue(new PathParser("/info/version")));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        
        assertThat(outputStreamClosed.get(), is(true));
        assertThat(responseContent.toString(), is(expectedVersion));
    }
    
    @Test public void
    responds_to_obsolete_request_for_status_page() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/xml");
            oneOf(response).close();
            
            allowing(request).getPath(); will(returnValue(new PathParser("/status")));
            allowing(statusSupplier).get(); will(returnValue(RUNNING));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        assertThat(responseContent.toString(), startsWith("<?xml"));
    }
    
    @Test public void
    responds_to_obsolete_request_for_status_page_css() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/css");
            oneOf(response).close();
            
            allowing(request).getPath(); will(returnValue(new PathParser("/status-page.css")));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        assertThat(responseContent.toString(), containsString("font-family:"));
    }
    
    @Test public void
    responds_to_obsolete_request_for_status_with_http_status_code_of_200_if_blondin_is_running() throws Exception {
        context.checking(new Expectations() {{
            never(response).setCode(with(not(200)));
            never(response).setText(with(not("OK")));
            oneOf(response).close();
            
            allowing(request).getPath(); will(returnValue(new PathParser("/status")));
            allowing(statusSupplier).get(); will(returnValue(RUNNING));
            ignoring(response).set(with(any(String.class)), with(any(String.class)));
        }});
        
        handler.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void
    responds_to_obsolete_request_for_status_with_http_status_code_of_503_if_blondin_is_suspended() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).setCode(503);
            oneOf(response).setText("Service Unavailable");
            oneOf(response).close();
            
            allowing(request).getPath(); will(returnValue(new PathParser("/status")));
            allowing(statusSupplier).get(); will(returnValue(SUSPENDED));
            ignoring(response).set(with(any(String.class)), with(any(String.class)));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
    }
}
