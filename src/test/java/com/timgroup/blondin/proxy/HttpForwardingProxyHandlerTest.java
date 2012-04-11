package com.timgroup.blondin.proxy;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.webbitserver.HttpRequest;
import org.webbitserver.stub.StubHttpControl;
import org.webbitserver.stub.StubHttpRequest;
import org.webbitserver.stub.StubHttpResponse;

import static com.timgroup.blondin.proxy.HttpForwardingProxyHandlerTest.HttpRequestMatcher.a_request_for;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public final class HttpForwardingProxyHandlerTest {

    private final Mockery context = new Mockery();
    
    private final HttpClient client = context.mock(HttpClient.class);
    private final HttpForwardingProxyHandler handler = new HttpForwardingProxyHandler("targetserver:2222", client);
    
    @Test public void
    forwards_request() throws Exception {
        final StubHttpRequest request = new StubHttpRequest().uri("/some/old/resource.html");
        final StubHttpResponse response = new StubHttpResponse();
        
        context.checking(new Expectations() {{
            oneOf(client).handle(with(a_request_for("http://targetserver:2222/some/old/resource.html")), with(sameInstance(response)));
        }});
        
        handler.handleHttpRequest(request, response, new StubHttpControl());
        context.assertIsSatisfied();
    }

    public static final class HttpRequestMatcher extends FeatureMatcher<HttpRequest, String> {
        private HttpRequestMatcher(Matcher<String> urlMatcher) {
            super(urlMatcher, "with url", "url");
        }
        public static HttpRequestMatcher a_request_for(String url) {
            return new HttpRequestMatcher(is(url));
        }
        @Override protected String featureValueOf(HttpRequest actual) { return actual.uri(); }
    }
}
