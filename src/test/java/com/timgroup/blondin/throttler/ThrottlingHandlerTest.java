package com.timgroup.blondin.throttler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public final class ThrottlingHandlerTest {

    private final Mockery context = new Mockery();

    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    @Test public void
    delegates_request_handling_to_decorated_handler() {
        final Container delegate = context.mock(Container.class);
        final ThrottlingHandler handler = new ThrottlingHandler(delegate, 1);
        
        context.checking(new Expectations() {{
            oneOf(delegate).handle(with(sameInstance(request)), with(sameInstance(response)));
        }});

        handler.handle(request, response);
        waitForCompletionOfTasks(handler, 1);
        
        context.assertIsSatisfied();
    }

    @Test public void
    throttles_simultaneous_requests_to_within_configured_bandwidth() {
        final BlockingContainer delegate = new BlockingContainer();
        
        final ThrottlingHandler handler = new ThrottlingHandler(delegate, 1);
        
        handler.handle(null, null);
        handler.handle(null, null);
        
        waitForReceiptOfTasks(handler, 2);
        waitForActiveTasks(handler, 1);
        assertThat(delegate.receivedRequests(), is(1));
        
        delegate.unblock();
        waitForCompletionOfTasks(handler, 2);
        
        assertThat(delegate.receivedRequests(), is(2));
    }

    private void waitForReceiptOfTasks(ThrottlingHandler handler, int count) {
        while(handler.receivedTaskCount() < count) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    private void waitForActiveTasks(ThrottlingHandler handler, int count) {
        while(handler.activeTaskCount() < count) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void waitForCompletionOfTasks(ThrottlingHandler handler, int count) {
        while(handler.completedTaskCount() < count) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static final class BlockingContainer implements Container {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicInteger receivedRequests = new AtomicInteger(0);

        public void unblock() {
            latch.countDown();
        }

        public int receivedRequests() {
            return receivedRequests.get();
        }

        @Override
        public void handle(Request req, Response resp) {
            try {
                receivedRequests.incrementAndGet();
                latch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
