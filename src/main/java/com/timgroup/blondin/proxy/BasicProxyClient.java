package com.timgroup.blondin.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public final class BasicProxyClient implements ProxyClient {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            final URL url = new URL(request.uri());
            final URLConnection conn = url.openConnection();
            final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            final StringBuilder responseText = new StringBuilder();
            String contentLine;
            while ((contentLine = in.readLine()) != null) {
                responseText.append(contentLine);
            }
            in.close();
            
            response.header("Content-type", "text/plain");
            response.write(responseText.toString());
            response.end();
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}