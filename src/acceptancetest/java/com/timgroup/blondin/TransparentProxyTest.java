package com.timgroup.blondin;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TransparentProxyTest {

    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        TrivialHttpServer.serving("/some/target/url", "hello, world").on(34297);
        
        new BlondinServer("localhost:34297", 23453);
        
        assertThat(TrivialHttpClient.contentFrom("http://localhost:23453/some/target/url"), is("hello, world"));
    }
}