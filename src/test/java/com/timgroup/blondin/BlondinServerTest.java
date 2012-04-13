package com.timgroup.blondin;

import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondinServerTest {

    @Test(timeout=1000) public void
    reports_startup_success() {
        final BlondinServer blondin = new BlondinServer(31415, "x:21");
        while(!blondin.running()) { }
        assertThat(blondin.running(), is(true));
    }
    
    @Test(timeout=1000) public void
    reports_shutdown_success() {
        final BlondinServer blondin = new BlondinServer(31416, "x:21");
        while(!blondin.running()) { }
        
        blondin.shutdown();
        while(blondin.running()) { }
        assertThat(blondin.running(), is(false));
    }
    
    @Test(timeout=1000) public void
    shuts_down_in_response_to_shutdown_post_request() throws Exception {
        final BlondinServer blondin = new BlondinServer(31417, "x:21");
        while(!blondin.running()) { }
        
        postTo("http://localhost:31417/shutdown");
        
        while(blondin.running()) { }
        assertThat(blondin.running(), is(false));
    }

    private void postTo(String urlString) throws Exception {
        URL url = new URL(urlString);
        Socket client = new Socket(url.getHost(), url.getPort());
        OutputStream http = client.getOutputStream();
        http.write(String.format("POST %s HTTP/1.1\r\nHost: www.example.com\r\n\r\n", url.getFile()).getBytes("UTF-8"));
        http.flush();
    }
}