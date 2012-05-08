package com.timgroup.blondin.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.DefensiveHandler;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Maps.filterKeys;

public final class ProxyingHandler implements Container {

    private final Monitor monitor;
    private final String targetHost;
    private final int targetPort;

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public static Container create(Monitor monitor, String targetHost, int targetPort) {
        return new DefensiveHandler(monitor, new ProxyingHandler(monitor, targetHost, targetPort));
    }
    
    private ProxyingHandler(Monitor monitor, String targetHost, int targetPort) {
        this.monitor = monitor;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    @Override
    public void handle(Request request, Response response) {
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
        }
        catch(IOException e) {
            monitor.logError(ProxyingHandler.class, "Failed to handle request for " + request.getAddress(), e);
        }
    }

    private void transferRequestHeaders(Request request, HttpURLConnection conn) {
        final Joiner joiner = Joiner.on(",");
        for (String headerName : request.getNames()) {
            final String headerValue = joiner.join(request.getValues(headerName));
            conn.setRequestProperty(headerName, headerValue);
            monitor.logInfo(ProxyingHandler.class, "---> " + headerName + ":" + headerValue);
            if ("X-Forwarded-Host".equals(headerName) && "hipmirror.youdevise.com".equals(headerValue)) {
                conn.setRequestProperty("X-Forwarded-Proto", "https");
            }
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
            final InputStream inputStream = conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST ? conn.getErrorStream() : conn.getInputStream();
            ByteStreams.copy(inputStream, response.getOutputStream());
            inputStream.close();
        }
        catch (IOException e) {
            monitor.logError(ProxyingHandler.class, "Failed to transfer content from " + conn.getURL(), e);
        }
    }

}