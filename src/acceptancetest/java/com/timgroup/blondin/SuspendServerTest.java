package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public final class SuspendServerTest extends BlondinAcceptanceTestBase {

    @Test public void
    responds_to_a_suspend_request() throws Exception {
        TrivialHttpClient.post(blondinUrl() + "/suspend");
        
        final String statusPageUrl = blondinUrl() + "/status";
        TrivialHttpClient.waitForResponseCode(statusPageUrl, not(200));
        assertThat(TrivialHttpClient.httpResponseCodeFrom(statusPageUrl), is(203));
    }
}