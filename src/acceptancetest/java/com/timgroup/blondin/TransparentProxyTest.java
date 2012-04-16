package com.timgroup.blondin;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TransparentProxyTest {

    private static int blondinPort = 23453;
    private static int targetPort = 34297;
    
    @Before
    public void startBlondin() throws Exception {
        Blondin.main(new String[] {String.valueOf(blondinPort), "localhost", String.valueOf(targetPort)});
        TrivialHttpClient.waitForSocket("localhost", blondinPort);
    }
    
    @After
    public void stopBlondin() throws Exception {
        TrivialHttpClient.post(format("http://localhost:%s/shutdown", blondinPort));
        blondinPort++;
        targetPort++;
    }
    
    @Test public void
    transparently_redirects_to_target_application() throws Exception {
        TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort);
        
        final String requestUrl = format("http://localhost:%s/some/target/url", blondinPort);
        assertThat(TrivialHttpClient.contentFrom(requestUrl), is("hello, world"));
    }
    
    @Test public void
    forwards_query_parameters_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort);
        
        TrivialHttpClient.contentFrom(format("http://localhost:%s/some/target/url?foo=bar&baz=bob", blondinPort));
        
        assertThat(server.query(), is("foo=bar&baz=bob"));
    }
    
    @Test public void
    forwards_headers_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort);
        
        TrivialHttpClient.contentFrom(format("http://localhost:%s/some/target/url?foo=bar&baz=bob", blondinPort), "Cookie", "bob=foo");
        
        assertThat(server.header("Cookie"), is("bob=foo"));
    }
}