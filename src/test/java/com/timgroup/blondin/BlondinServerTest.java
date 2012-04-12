package com.timgroup.blondin;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondinServerTest {

    @Test(timeout=1000) public void
    reports_startup_success() {
        final BlondinServer blondin = new BlondinServer("x:21", 31415);
        while(!blondin.running()) { }
        assertThat(blondin.running(), is(true));
    }
    
    @Test(timeout=1000) public void
    reports_shutdown_success() {
        final BlondinServer blondin = new BlondinServer("x:21", 31416);
        while(!blondin.running()) { }
        
        blondin.shutdown();
        while(blondin.running()) { }
        assertThat(blondin.running(), is(false));
    }
    
    @Test(timeout=1000) public void
    shuts_down_in_response_to_shutdown_post_request() throws Exception {
        final BlondinServer blondin = new BlondinServer("x:21", 31417);
        while(!blondin.running()) { }
        
        postTo("http://localhost:31417/shutdown");
        
        while(blondin.running()) { }
        assertThat(blondin.running(), is(false));
    }

    private void postTo(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-Type", "text/plain");
        httpCon.setRequestProperty("Content-Length", "0");
        httpCon.setUseCaches(false);
        
        OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
        out.write("");
        out.close();
        
        httpCon.getContentLength();
        //httpCon.getResponseCode();
    }
}