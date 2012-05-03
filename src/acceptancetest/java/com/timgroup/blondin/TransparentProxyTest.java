package com.timgroup.blondin;


import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;
import com.timgroup.blondin.testutil.TrivialHttpClient;
import com.timgroup.blondin.testutil.TrivialHttpClient.TrivialResponse;
import com.timgroup.blondin.testutil.TrivialHttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TransparentProxyTest extends BlondinAcceptanceTestBase {

    @Test public void
    transparently_proxies_to_target_application() throws Exception {
        TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        final String requestUrl = blondinUrl() + "/some/target/url";
        assertThat(TrivialHttpClient.getFrom(requestUrl).content, is("hello, world"));
    }
    
    @Test public void
    forwards_query_parameters_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        TrivialHttpClient.getFrom(blondinUrl() +"/some/target/url?foo=bar&baz=bob");
        
        assertThat(server.query(), is("foo=bar&baz=bob"));
    }
    
    @Test public void
    forwards_headers_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url", "Cookie", "bob=foo");
        
        assertThat(server.header("Cookie"), is("bob=foo"));
    }
    
    @Ignore("Pending Implementation")
    @Test public void
    preserves_host_with_proxied_get_request() throws Exception {
        final TrivialHttpServer server = TrivialHttpServer.serving("/some/target/url", "hello, world").on(targetPort());
        
        final String requestUrl = "http://127.0.0.1:" + blondinPort() + "/some/target/url";
        TrivialHttpClient.getFrom(requestUrl);
        
        assertThat(server.requestUrl(), is(requestUrl));
    }
    
    @Test public void
    does_not_follow_redirects() throws Exception {
        TrivialHttpServer.servingRedirect("/some/target/url", "hello, world").on(targetPort());
        
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url");
        assertThat(response.code, is(302));
        assertThat(response.content, is("hello, world"));
    }
    
    @Test public void
    handles_a_404_transparently() throws Exception {
        TrivialHttpServer.serving("/some/target/url", "hello, world", 404).on(targetPort());
        
        final TrivialResponse response = TrivialHttpClient.getFrom(blondinUrl() + "/some/target/url");
        assertThat(response.code, is(404));
        assertThat(response.content, is("hello, world"));
    }
}