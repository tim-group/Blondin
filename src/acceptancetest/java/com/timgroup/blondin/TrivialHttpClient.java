package com.timgroup.blondin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public final class TrivialHttpClient {
    public static String contentFrom(final String urlString) throws IOException {
        final URL url = new URL(urlString);
        final URLConnection conn = url.openConnection();
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
        long startTime = System.currentTimeMillis();
        boolean available = false;
        while(!available) {
            try {
                Socket socket = new Socket(host, port);
                available = true;
                socket.close();
            } catch (IOException e) {
                if (System.currentTimeMillis() - startTime > 1000L) {
                    throw e;
                }
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
}