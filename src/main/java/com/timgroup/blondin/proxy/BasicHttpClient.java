package com.timgroup.blondin.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import com.google.common.io.ByteStreams;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Maps.filterKeys;

public final class BasicHttpClient implements HttpClient {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            final URL url = new URL(request.uri());
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            
            response.status(conn.getResponseCode());
            
            transferHeaders(response, conn);
            defensivelyTransferContent(response, conn);
            
            conn.disconnect();
            response.end();
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void transferHeaders(HttpResponse response, final HttpURLConnection conn) {
        for(Entry<String, List<String>> entry : filterKeys(conn.getHeaderFields(), notNull()).entrySet()) {
            for (String value : entry.getValue()) {
                response.header(entry.getKey(), value);
            }
        }
    }

    private void defensivelyTransferContent(HttpResponse response, final HttpURLConnection conn) throws IOException {
        final InputStream inputStream = conn.getInputStream();
        final byte[] content = ByteStreams.toByteArray(inputStream);
        inputStream.close();
        response.content(content);
    }
}