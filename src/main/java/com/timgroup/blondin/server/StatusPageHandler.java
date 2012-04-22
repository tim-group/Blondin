package com.timgroup.blondin.server;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.io.ByteStreams;
import com.timgroup.blondin.server.status.BlondinStatus;
import com.timgroup.status.Status;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public final class StatusPageHandler implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusPageHandler.class);

    private final BlondinStatus status;
    private final Supplier<BlondinServerStatus> serverStatusSupplier;

    public StatusPageHandler(Supplier<BlondinServerStatus> serverStatusSupplier,
                             Supplier<Iterable<String>> expensiveResourcesListSupplier)
    {
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
            LOGGER.error("Failed to respond to status page request", e);
        }
    }

    private void writeStatusPageCssTo(Response response) {
        try {
            response.set("Content-Type", "text/css");
            ByteStreams.copy(Status.class.getResourceAsStream("status-page.css"), response.getOutputStream());
            response.close();
        } catch (IOException e) {
            LOGGER.error("Failed to respond to status page css request", e);
        }
    }
}
