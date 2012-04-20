package com.timgroup.blondin.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.Matchers.not;

import static com.google.common.base.Charsets.UTF_8;
import static com.timgroup.blondin.server.BlondinServerStatus.RUNNING;
import static com.timgroup.blondin.server.BlondinServerStatus.STOPPED;
import static com.timgroup.blondin.server.BlondinServerStatus.SUSPENDED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondinServerTest {

    @Test(timeout=5000) public void
    reports_startup_success() throws Exception {
        final BlondinServer blondin = new BlondinServer(21415, "x", -1, null);
        waitForStatus(blondin, equalTo(RUNNING));
        assertThat(blondin.status(), is(RUNNING));
    }
    
    @Test(timeout=5000) public void
    reports_stop_success() throws Exception {
        final BlondinServer blondin = new BlondinServer(21416, "x", -1, null);
        waitForStatus(blondin, equalTo(RUNNING));
        
        blondin.stop();
        waitForStatus(blondin, not(RUNNING));
        assertThat(blondin.status(), is(STOPPED));
    }
    
    @Test(timeout=5000) public void
    shuts_down_in_response_to_stop_post_request() throws Exception {
        final BlondinServer blondin = new BlondinServer(21417, "x", -1, null);
        waitForStatus(blondin, equalTo(RUNNING));
        
        while(RUNNING.equals(blondin.status())) {
            postTo("http://localhost:21417/stop");
            Thread.sleep(100L);
        }
        
        assertThat(blondin.status(), is(STOPPED));
    }
    
    @Test(timeout=5000) public void
    suspends_in_response_to_suspend_post_request() throws Exception {
        final BlondinServer blondin = new BlondinServer(21418, "x", -1, null);
        waitForStatus(blondin, equalTo(RUNNING));
        
        while(RUNNING.equals(blondin.status())) {
            postTo("http://localhost:21418/suspend");
            Thread.sleep(100L);
        }
        
        assertThat(blondin.status(), is(SUSPENDED));
    }

    private void postTo(String urlString) {
        try {
            URL url = new URL(urlString);
            Socket client = new Socket(url.getHost(), url.getPort());
            OutputStream http = client.getOutputStream();
            http.write(String.format("POST %s HTTP/1.1\r\nHost: www.example.com\r\n\r\n", url.getFile()).getBytes(UTF_8.name()));
            http.flush();
        } catch(UnknownHostException e) {
            
        } catch(IOException e) {
            
        }
    }
    
    private void waitForStatus(BlondinServer blondin, Matcher<BlondinServerStatus> statusMatcher) throws Exception {
        while(!statusMatcher.matches(blondin.status())) {
            Thread.sleep(50L);
        }
    }
}