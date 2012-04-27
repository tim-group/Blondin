package com.timgroup.blondin;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.Sockets;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;
import com.timgroup.blondin.testutil.TrivialHttpServer;

public final class RobustConnectionManagementTest extends BlondinAcceptanceTestBase {

    @Test(timeout=5000) public void
    closes_client_connection_when_proxying_an_uncontactable_server() throws Exception {
        Future<TrivialResponse> response = TrivialHttpClient.getFromInBackground(blondinUrl() + "/some/target/url");
        waitForAClosedResponse(response);
    }

    private void waitForAClosedResponse(Future<TrivialResponse> response) throws InterruptedException, ExecutionException {
        response.get();
    }

    @Ignore("pending acceptance test until we investigate why simple web isn't forwaring the request")
    @Test(timeout=5000) public void
    proxies_a_request_that_immediately_closes() throws Exception {
        TrivialHttpServer server = TrivialHttpServer.serving("/my/cheap/resource", "hello, world").on(targetPort());
        Sockets.waitForSocket("localhost", targetPort());
        
        ClientConnection conn = ClientConnection.makeRequestFor("/my/cheap/resource", blondinPort());
        conn.disconnect();
        waitForRequestsToBeFowardedToServer(server, 1);
    }

    @Ignore("unfinished pending acceptance test: TODO ensure that blondin itself has finished proxying disconnected request and that " +
            "it thinks no problems have happened")
    @Test(timeout=5000) public void
    closes_server_connection_when_a_client_connection_breaks_midway_through_response_delivery() throws Exception {
        final CountDownLatch serverSendResponseTrigger = new CountDownLatch(1);
        TrivialHttpServer server = TrivialHttpServer.serving("/my/cheap/resource", "hello, world").on(targetPort())
                                                    .blockingFirst(1, serverSendResponseTrigger);
        Sockets.waitForSocket("localhost", targetPort());
        
        ClientConnection conn = ClientConnection.makeRequestFor("/my/cheap/resource", blondinPort());
        waitForRequestsToBeFowardedToServer(server, 1);
        conn.disconnect();
        
        serverSendResponseTrigger.countDown();
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

    private static final class ClientConnection {
        
        private final Socket client;
        private final PrintWriter output;

        public static ClientConnection makeRequestFor(String urlPath, int port) throws IOException {
            return new ClientConnection(urlPath, port);
        }
        
        private ClientConnection(String urlPath, int port) throws IOException {
            client = new Socket("localhost", port);
            output = new PrintWriter(client.getOutputStream());
            
            output.print("GET " + urlPath + " HTTP/1.0\r\n");
            output.print("\r\n");
            output.flush();
        }
        
        public void disconnect() throws IOException {
            output.close();
            client.close();
        }
    }

}
