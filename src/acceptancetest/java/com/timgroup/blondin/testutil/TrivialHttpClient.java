package com.timgroup.blondin.testutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import org.hamcrest.Matcher;

public final class TrivialHttpClient {
    
    public static final class TrivialResponse {
        public final int code;
        public final String content;
        public TrivialResponse(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }
    
    public static Future<TrivialResponse> getFromInBackground(final String urlString) throws IOException {
        final CallableGetRequest request = new CallableGetRequest(urlString);
        final FutureTask<TrivialResponse> future = new FutureTask<TrivialResponse>(request) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                request.cancel();
                return super.cancel(mayInterruptIfRunning);
            }
        };
        new Thread(future).start();
        return future;
    }
    
    public static TrivialResponse getFrom(final String urlString) throws IOException {
        return new CallableGetRequest(urlString).call();
    }
    
    public static TrivialResponse getFrom(String urlString, String headerName, String headerValue) throws IOException {
        return new CallableGetRequest(urlString, headerName, headerValue).call();
    }
    
    public static void post(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
        httpCon.setInstanceFollowRedirects(false);
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
        Sockets.waitForSocket(url.getHost(), url.getPort());
        
        long startTime = System.currentTimeMillis();
        boolean codeMatched = false;
        while(!codeMatched) {
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setInstanceFollowRedirects(false);
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
        conn.setInstanceFollowRedirects(false);
        final int result = conn.getResponseCode();
        conn.disconnect();
        return result;
    }

    private static final class CallableGetRequest implements Callable<TrivialResponse> {
        private final String urlString;
        private final String headerName;
        private final String headerValue;

        private final AtomicReference<HttpURLConnection> connnection = new AtomicReference<HttpURLConnection>();

        public CallableGetRequest(String urlString) {
            this(urlString, "a", "b");
        }

        public CallableGetRequest(String urlString, String headerName, String headerValue) {
            this.urlString = urlString;
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public TrivialResponse call() throws IOException {
            try {
                final URL url = new URL(urlString);
                final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                connnection.set(conn);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty(headerName, headerValue);
                Sockets.waitForSocket(url.getHost(), url.getPort());
                final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream()));
                final int responseCode = conn.getResponseCode();
                
                final StringBuilder responseText = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseText.append(inputLine);
                }
                in.close();
                return new TrivialResponse(responseCode, responseText.toString());
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public void cancel() {
            final HttpURLConnection conn = connnection.get();
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}