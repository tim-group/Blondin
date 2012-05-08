package com.timgroup.blondin.server;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.google.common.base.Supplier;
import com.google.common.io.ByteStreams;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.status.BlondinStatus;
import com.timgroup.tucker.info.status.StatusPage;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public final class StatusPageHandler implements Container {

    private final Monitor monitor;
    private final BlondinStatus status;
    private final Supplier<BlondinServerStatus> serverStatusSupplier;

    public StatusPageHandler(Monitor monitor,
                             Supplier<BlondinServerStatus> serverStatusSupplier,
                             Supplier<Iterable<String>> expensiveResourcesListSupplier)
    {
        this.monitor = monitor;
        this.serverStatusSupplier = serverStatusSupplier;
        this.status = new BlondinStatus(expensiveResourcesListSupplier);
    }

    @Override
    public void handle(Request request, Response response) {
        if (request.getPath().getPath().equals("/status-page.css")) {
            writeStatusPageCssTo(response);
            return;
        }
        
        if (BlondinServerStatus.SUSPENDED.equals(serverStatusSupplier.get())) {
            response.setCode(HTTP_UNAVAILABLE);
            response.setText("Service Unavailable");
        }
        response.set("Content-Type", "text/xml");
        try {
            status.writeTo(response.getOutputStream());
            response.close();
        } catch (IOException e) {
            monitor.logError(StatusPageHandler.class, "Failed to respond to status page request", e);
        }
    }

    private void writeStatusPageCssTo(Response response) {
        try {
            response.set("Content-Type", "text/css");
            ByteStreams.copy(StatusPage.class.getResourceAsStream("status-page.css"), response.getOutputStream());
            response.close();
        } catch (IOException e) {
            monitor.logError(StatusPageHandler.class, "Failed to respond to status page css request", e);
        }
    }
}
