package com.timgroup.blondin;


import org.junit.Ignore;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class DeploymentContractTest extends BlondinAcceptanceTestBase {

    @Ignore("Pending Implementation")
    @Test public void
    serves_application_version() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/version");
        
        assertThat(response.code, is(200));
        assertThat(response.contentType, is("text/plain"));
        assertThat(response.content, is("1.0.0"));
    }

    @Ignore("Pending Implementation")
    @Test public void
    serves_application_health() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/health");
        
        assertThat(response.code, is(200));
        assertThat(response.contentType, is("text/plain"));
        assertThat(response.content, is("healty"));
    }

    @Ignore("Pending Implementation")
    @Test public void
    serves_application_stoppability() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/info/stoppable");
        
        assertThat(response.code, is(200));
        assertThat(response.contentType, is("text/plain"));
        assertThat(response.content, is("safe"));
    }
}