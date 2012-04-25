package com.timgroup.blondin;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;

public final class RobustConnectionManagementTest extends BlondinAcceptanceTestBase {

    @Ignore("pending implementation")
    @Test(timeout=5000) public void
    closes_client_connection_when_proxying_an_uncontactable_server() throws Exception {
        Future<TrivialResponse> response = TrivialHttpClient.getFromInBackground(blondinUrl() + "/some/target/url");
        waitForAClosedResponse(response);
    }

    private void waitForAClosedResponse(Future<TrivialResponse> response) throws InterruptedException, ExecutionException {
        response.get();
    }

}
