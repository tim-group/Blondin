package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.server.handler.AppInfoHandler;
import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public final class StatusPageTest extends BlondinAcceptanceTestBase {

    @Test public void
    serves_a_status_page() throws Exception {
        final String currentVersion = AppInfoHandler.class.getPackage().getImplementationVersion();
        final String expectedVersionString = (null == currentVersion) ? "" : ": <value>" + currentVersion + "</value>";
        
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/status");
        
        assertThat(response.code, is(200));
        assertThat(response.content, startsWith("<?xml version=\"1.0\" ?>"));
        assertThat(response.content, containsString("Version" + expectedVersionString));
    }
}