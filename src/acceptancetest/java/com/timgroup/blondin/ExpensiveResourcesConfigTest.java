package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public final class ExpensiveResourcesConfigTest extends BlondinAcceptanceTestBase {

    @Override
    protected void beforeBlondinStarts() throws Exception {
        TrivialHttpServer.serving(expensiveResourcesPath(), "my/{catchy}/res1\nmy/res2").on(targetPort());
    }
    
    @Test public void
    retrieves_list_of_expensive_resources_via_get_request() throws Exception {
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/status");
        
        assertThat(response.content, containsString("my/{catchy}/res1"));
        assertThat(response.content, containsString("my/res2"));
    }
}