package com.timgroup.blondin;


import java.io.IOException;
import java.util.List;
import java.util.Properties;
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

    private TrivialHttpServer server = null;
    
    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        expensiveResources.add("/my/expensive/resource");
    }
    
    @After
    public void cleanUp() {
        if (server != null) {
            server.unblock();
        }
    }
    
    @Test public void
    fulfils_multiple_normal_requests_simultaneously() throws Exception {
        final int blockedRequests = 49;
        server = TrivialHttpServer.on(targetPort()).serving("/my/cheap/resource", "hello, world")
                                  .blockingFirst(blockedRequests);
        
        Future<TrivialResponse> lastReseponse = issueBackgroundRequests(blockedRequests, blondinUrl() + "/my/cheap/resource");
        
        while(server.fulfilling() < blockedRequests) { };
        assertThat(TrivialHttpClient.getFrom(blondinUrl() + "/my/cheap/resource").code, is(200));
        
        server.unblock();
        assertThat(lastReseponse.get().code, is(200));
    }

    @Test public void
    throttles_simultaneous_requests_to_expensive_resources() throws Exception {
        server = TrivialHttpServer.on(targetPort()).serving("/my/expensive/resource", "hello, world").blockingAll()
                                                   .servingUnblockably("/my/cheap/resource", "cheap", 200);
        
        issueBackgroundRequests(16, blondinUrl() + "/my/expensive/resource");
        while(server.fulfilling() < 16) { };
        
        Future<TrivialResponse> throttledRequest = TrivialHttpClient.getFromInBackground(blondinUrl() + "/my/expensive/resource");
        assertThat(TrivialHttpClient.getFrom(blondinUrl() + "/my/cheap/resource").code, is(200));
        assertThat(server.totalRequestsReceived(), is(17));
        
        server.unblock();
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
