package com.timgroup.blondin;


import java.util.concurrent.Future;

import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class RobustConnectionManagementTest extends BlondinAcceptanceTestBase {

    @Test(timeout=5000) public void
    closes_client_connection_when_proxying_an_uncontactable_server() throws Exception {
        TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url");
    }

    @Test(timeout=5000) public void
    closes_server_connection_when_a_client_connection_breaks_midway_through_response_delivery() throws Exception {
        TrivialHttpServer server = TrivialHttpServer.on(targetPort())
                                                    .serving("/my/cheap/resource", "hello, world")
                                                    .blockingAll();
        
        Future<TrivialResponse> request = TrivialHttpClient.getFromInBackground(blondinUrl() + "/my/cheap/resource");
        
        waitForRequestsToBeFowardedToServer(server, 1);
        request.cancel(true);
        
        server.unblock();
        waitForServerToFinishDeliveringRequests(server, 1);
    }

    private void waitForServerToFinishDeliveringRequests(TrivialHttpServer server, int expectedNumber) {
        while (server.totalRequestsFulfilled() < expectedNumber) {
            Thread.yield();
        };
        assertThat(server.totalRequestsFulfilled(), is(expectedNumber));
    }

    private void waitForRequestsToBeFowardedToServer(TrivialHttpServer server, int expectedNumber) {
        while (server.totalRequestsReceived() < expectedNumber) {
            Thread.yield();
        };
    }
}
