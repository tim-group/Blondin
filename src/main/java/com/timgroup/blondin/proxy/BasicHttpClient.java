package com.timgroup.blondin.proxy;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map.Entry;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import com.google.common.io.ByteStreams;

public final class BasicHttpClient implements HttpClient {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            final URL url = new URL(request.uri());
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            final InputStream inputStream = conn.getInputStream();
            
            response.header("Content-type", "text/plain");
            response.status(conn.getResponseCode());
            response.content(ByteStreams.toByteArray(inputStream));
            
            inputStream.close();
            response.end();
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}