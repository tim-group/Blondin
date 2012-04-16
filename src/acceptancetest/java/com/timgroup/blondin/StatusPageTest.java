package com.timgroup.blondin;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

public final class StatusPageTest extends BlondinAcceptanceTestBase {

    @Test public void
    serves_a_status_page() throws Exception {
        final String requestUrl = blondinUrl() + "/status";
        assertThat(TrivialHttpClient.contentFrom(requestUrl), startsWith("<?xml version=\"1.0\" ?>"));
    }
}