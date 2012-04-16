package com.timgroup.blondin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import org.hamcrest.Matcher;

public final class TrivialHttpClient {
    
    public static String contentFrom(final String urlString) throws IOException {
        return contentFrom(urlString, "a", "b");
    }
    
    public static String contentFrom(String urlString, String headerName, String headerValue) throws IOException {
        final URL url = new URL(urlString);
        final URLConnection conn = url.openConnection();
        conn.setRequestProperty(headerName, headerValue);
        waitForSocket(url.getHost(), url.getPort());
        final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
        final StringBuilder responseText = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseText.append(inputLine);
        }
        in.close();
        return responseText.toString();
    }

    public static void waitForSocket(String host, int port) throws IOException {
        waitForSocket(host, port, true);
    }
    
    public static void waitForNoSocket(String host, int port) throws IOException {
        waitForSocket(host, port, false);
    }
    
    public static void waitForSocket(String host, int port, boolean desiredState) throws IOException {
        long startTime = System.currentTimeMillis();
        boolean currentState = !desiredState;
        while(currentState != desiredState) {
            try {
                Socket socket = new Socket(host, port);
                currentState = true;
                socket.close();
            } catch (IOException e) {
                currentState = false;
            }
            if (System.currentTimeMillis() - startTime > 10000L) {
                throw new IllegalStateException("socket did not " + (desiredState ? "open" : "close"));
            }
        }
    }
    
    public static boolean isSocketOpen(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void post(String urlString) throws IOException {
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
    }

    public static void waitForResponseCode(String urlString, Matcher<Integer> codeMatcher) throws IOException {
        final URL url = new URL(urlString);
        waitForSocket(url.getHost(), url.getPort());
        
        long startTime = System.currentTimeMillis();
        boolean codeMatched = false;
        while(!codeMatched) {
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            codeMatched = codeMatcher.matches(conn.getResponseCode());
            conn.disconnect();
            
            if (System.currentTimeMillis() - startTime > 10000L) {
                throw new IllegalStateException("response code did not reach expected state");
            }
        }
    }

    public static int httpResponseCodeFrom(String urlString) throws IOException {
        final URL url = new URL(urlString);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        final int result = conn.getResponseCode();
        conn.disconnect();
        return result;
    }

}