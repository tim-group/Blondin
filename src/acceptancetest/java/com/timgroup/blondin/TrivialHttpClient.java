package com.timgroup.blondin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public final class TrivialHttpClient {
    public static String contentFrom(final String balancerUrlString) throws MalformedURLException, IOException {
        final URL url = new URL(balancerUrlString);
        final URLConnection conn = url.openConnection();
        final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
        final StringBuilder responseText = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseText.append(inputLine);
        }
        in.close();
        return responseText.toString();
    }
}