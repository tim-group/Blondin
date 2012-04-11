package com.timgroup.blondin.proxy;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import com.google.common.io.ByteStreams;

public final class BasicHttpClient implements HttpClient {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            final URL url = new URL(request.uri());
            final URLConnection conn = url.openConnection();
            response.header("Content-type", "text/plain");
            final InputStream inputStream = conn.getInputStream();
            response.content(ByteStreams.toByteArray(inputStream));
            inputStream.close();
            response.end();
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}