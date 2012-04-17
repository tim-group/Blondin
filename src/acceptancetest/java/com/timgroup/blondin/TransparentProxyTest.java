package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TransparentProxyTest extends BlondinAcceptanceTestBase {

    @Test public void
    transparently_proxies_to_target_application() throws Exception {
        TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        final String requestUrl = blondinUrl() + "/some/target/url";
        assertThat(TrivialHttpClient.contentFrom(requestUrl), is("hello, world"));
    }
    
    @Test public void
    forwards_query_parameters_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        TrivialHttpClient.contentFrom(blondinUrl() +"/some/target/url?foo=bar&baz=bob");
        
        assertThat(server.query(), is("foo=bar&baz=bob"));
    }
    
    @Test public void
    forwards_headers_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        TrivialHttpClient.contentFrom(blondinUrl() + "/some/target/url?foo=bar&baz=bob", "Cookie", "bob=foo");
        
        assertThat(server.header("Cookie"), is("bob=foo"));
    }
    
    @Test public void
    does_not_follow_redirects() throws Exception {
        TrivialHttpServer.servingRedirect("/some/target/url", "hello, world").on(targetPort());
        
        final String requestUrl = blondinUrl() + "/some/target/url";
        assertThat(TrivialHttpClient.contentFrom(requestUrl), is("hello, world"));
    }
}