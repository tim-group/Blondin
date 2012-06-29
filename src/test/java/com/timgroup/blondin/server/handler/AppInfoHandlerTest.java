package com.timgroup.blondin.server.handler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.timgroup.blondin.DummyMonitor;

public final class AppInfoHandlerTest {

    private final Mockery context = new Mockery();
    
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    private final AtomicBoolean outputStreamClosed = new AtomicBoolean(false);
    private final OutputStream responseContent = new ByteArrayOutputStream() {
        @Override
        public void close() throws java.io.IOException { outputStreamClosed.set(true); super.close(); };
    };

    private final List<String> blackList = Lists.newArrayList();
    private final AppInfoHandler handler = new AppInfoHandler(new DummyMonitor(), Suppliers.<Iterable<String>>ofInstance(blackList));
    
    @Before
    public void attachResponseContent() throws Exception {
        context.checking(new Expectations() {{
            allowing(response).getOutputStream(); will(returnValue(responseContent));
            atLeast(1).of(response).close();
        }});
    }
    
    @Test public void
    responds_to_request_for_version() throws Exception {
        final String expectedVersion = Strings.nullToEmpty(AppInfoHandler.class.getPackage().getImplementationVersion());
        
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
    responds_to_request_for_health() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/plain");
            oneOf(response).add("Content-Type", "charset=UTF-8");
            
            allowing(request).getPath(); will(returnValue(new PathParser("/info/health")));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        
        assertThat(outputStreamClosed.get(), is(true));
        assertThat(responseContent.toString(), is("healthy"));
    }
    
    @Test public void
    responds_to_request_for_stoppability() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/plain");
            oneOf(response).add("Content-Type", "charset=UTF-8");
            
            allowing(request).getPath(); will(returnValue(new PathParser("/info/stoppable")));
        }});
        
        handler.handle(request, response);
        
        context.assertIsSatisfied();
        
        assertThat(outputStreamClosed.get(), is(true));
        assertThat(responseContent.toString(), is("safe"));
    }
}
