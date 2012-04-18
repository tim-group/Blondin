package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;

import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.containsString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

public final class StatusPageTest extends BlondinAcceptanceTestBase {

    @Test public void
    serves_a_status_page() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/status");
        
        assertThat(response.code, is(200));
        assertThat(response.content, startsWith("<?xml version=\"1.0\" ?>"));
        assertThat(response.content, containsString("Version: <value>"));
    }
}