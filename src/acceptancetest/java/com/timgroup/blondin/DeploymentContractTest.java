package com.timgroup.blondin;


import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Strings;
import com.timgroup.blondin.server.StatusPageHandler;
import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public final class DeploymentContractTest extends BlondinAcceptanceTestBase {

    @Test public void
    serves_application_version() throws Exception {
        final String expectedVersion = Strings.nullToEmpty(StatusPageHandler.class.getPackage().getImplementationVersion());
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/version");
        
        assertThat(response.code, is(200));
        assertThat(response.header("Content-Type"), contains("charset=UTF-8", "text/plain"));
        assertThat(response.content, is(expectedVersion));
    }

    @Ignore("Pending Implementation")
    @Test public void
    serves_application_health() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/health");
        
        assertThat(response.code, is(200));
        assertThat(response.header("Content-Type"), contains("charset=UTF-8", "text/plain"));
        assertThat(response.content, is("healty"));
    }

    @Ignore("Pending Implementation")
    @Test public void
    serves_application_stoppability() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/stoppable");
        
        assertThat(response.code, is(200));
        assertThat(response.header("Content-Type"), contains("charset=UTF-8", "text/plain"));
        assertThat(response.content, is("safe"));
    }
}