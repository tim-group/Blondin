package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;

import static org.hamcrest.Matchers.containsString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

public final class StatusPageTest extends BlondinAcceptanceTestBase {

    @Test public void
    serves_a_status_page() throws Exception {
        final String statusPageContent = TrivialHttpClient.contentFrom(blondinUrl() + "/status");
        
        assertThat(statusPageContent, startsWith("<?xml version=\"1.0\" ?>"));
        assertThat(statusPageContent, containsString("Version: <value>"));
    }
}