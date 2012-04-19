package com.timgroup.blondin;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Ignore;
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
    
    @Ignore("pending implementation")
    @Test public void
    fulfils_multiple_normal_requests_simultaneously() throws Exception {
        TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort()).blockingFirst(1, trigger);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url");
                }
                catch (IOException e) { }
            }
        }).start();
        
        while(server.fulfilling() < 1) { };
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url");
        assertThat(response.code, is(200));
    }
}
