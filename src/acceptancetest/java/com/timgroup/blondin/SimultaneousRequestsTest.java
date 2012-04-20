package com.timgroup.blondin;


import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class SimultaneousRequestsTest extends BlondinAcceptanceTestBase {

    private final CountDownLatch trigger = new CountDownLatch(1);
    
    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        expensiveResources.add("/my/expensive/resource");
    }
    
    @After
    public void cleanUp() {
       trigger.countDown();
    }
    
    @Test public void
    fulfils_multiple_normal_requests_simultaneously() throws Exception {
        final int blockedRequests = 49;
        TrivialHttpServer server = TrivialHttpServer.serving("/my/cheap/resource", "hello, world").on(targetPort())
                                                    .blockingFirst(blockedRequests, trigger);
        
        Future<TrivialResponse> lastReseponse = issueBackgroundRequests(blockedRequests, blondinUrl() + "/my/cheap/resource");
        
        while(server.fulfilling() < blockedRequests) { };
        assertThat(TrivialHttpClient.getFrom(blondinUrl() + "/my/cheap/resource").code, is(200));
        
        trigger.countDown();
        assertThat(lastReseponse.get().code, is(200));
    }

    @Test public void
    throttles_simultaneous_requests_to_expensive_resources() throws Exception {
        TrivialHttpServer server = TrivialHttpServer.serving("/my/expensive/resource", "hello, world").on(targetPort())
                                                    .blockingFirst(100, trigger);
        
        issueBackgroundRequests(16, blondinUrl() + "/my/expensive/resource");
        while(server.fulfilling() < 16) { };
        
        Future<TrivialResponse> throttledRequest = TrivialHttpClient.getFromInBackground(blondinUrl() + "/my/expensive/resource");
        assertThat(TrivialHttpClient.getFrom(blondinUrl() + "/default").code, is(200));
        assertThat(server.totalRequestsReceived(), is(17));
        
        trigger.countDown();
        assertThat(throttledRequest.get().code, is(200));
        assertThat(server.totalRequestsReceived(), is(18));
    }

    private Future<TrivialResponse> issueBackgroundRequests(int count, String url) throws IOException {
        for (int i = 0; i < count - 1; i++) {
            TrivialHttpClient.getFromInBackground(url);
        }
        return TrivialHttpClient.getFromInBackground(url);
    }
}
