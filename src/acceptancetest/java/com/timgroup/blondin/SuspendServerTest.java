package com.timgroup.blondin;


import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public final class SuspendServerTest extends BlondinAcceptanceTestBase {

    @Ignore("Pending Implementation")
    @Test public void
    responds_to_a_suspend_request() throws Exception {
        TrivialHttpClient.post(blondinUrl() + "/suspend");
        
        final String statusPageUrl = blondinUrl() + "/status";
        TrivialHttpClient.waitForResponseCode(statusPageUrl, not(200));
        assertThat(TrivialHttpClient.httpResponseCodeFrom(statusPageUrl), is(300));
    }
}