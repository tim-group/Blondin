package com.timgroup.blondin.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Maps.filterKeys;

public final class BasicHttpClient implements HttpClient {

    @Override
    public void handle(String targetHost, int targetPort, Request request, Response response) {
        try {
            final URL url = new URL("http", targetHost, targetPort, request.getAddress().toString());
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setInstanceFollowRedirects(false);
            
            transferRequestHeaders(request, conn);
            
            response.setCode(conn.getResponseCode());
            response.setText(conn.getResponseMessage());

            transferResponseHeaders(response, conn);
            response.commit();
            
            defensivelyTransferContent(response, conn);
            conn.disconnect();
            response.close();
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void transferRequestHeaders(Request request, HttpURLConnection conn) {
        final Joiner joiner = Joiner.on(",");
        for (String headerName : request.getNames()) {
            conn.setRequestProperty(headerName, joiner.join(request.getValues(headerName)));
        }
    }

    private void transferResponseHeaders(Response response, final HttpURLConnection conn) {
        for(Entry<String, List<String>> entry : filterKeys(conn.getHeaderFields(), notNull()).entrySet()) {
            for (String value : entry.getValue()) {
                response.add(entry.getKey(), value);
            }
        }
    }

    private void defensivelyTransferContent(Response response, final HttpURLConnection conn) throws IOException {
        try {
            final InputStream inputStream = conn.getInputStream();
            ByteStreams.copy(inputStream, response.getOutputStream());
            inputStream.close();
        }
        catch (IOException e) {
        }
    }
}