package com.timgroup.blondin;


import java.io.IOException;
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
    
    @After
    public void cleanUp() {
       trigger.countDown();
    }
    
    @Test public void
    fulfils_multiple_normal_requests_simultaneously() throws Exception {
        final int simultaneousRequests = 8;
        TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort())
                                                    .blockingFirst(simultaneousRequests - 1, trigger);
        
        Future<TrivialResponse> lastReseponse = issueBackgroundRequests(simultaneousRequests - 1, blondinUrl() + "/some/target/url");
        
        while(server.fulfilling() < simultaneousRequests - 1) { };
        assertThat(TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url").code, is(200));
        
        trigger.countDown();
        assertThat(lastReseponse.get().code, is(200));
    }

    private Future<TrivialResponse> issueBackgroundRequests(int count, String url) throws IOException {
        for (int i = 0; i < count - 1; i++) {
            TrivialHttpClient.getFromInBackground(url);
        }
        return TrivialHttpClient.getFromInBackground(url);
    }
}
