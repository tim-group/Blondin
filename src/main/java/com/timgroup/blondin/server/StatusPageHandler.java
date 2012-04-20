package com.timgroup.blondin.server;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.google.common.base.Supplier;
import com.timgroup.blondin.server.status.BlondinStatus;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public final class StatusPageHandler implements Container {

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
        if (BlondinServerStatus.SUSPENDED.equals(serverStatusSupplier.get())) {
            response.setCode(HTTP_UNAVAILABLE);
            response.setText("Service Unavailable");
        }
        response.set("Content-Type", "text/xml");
        try {
            status.writeTo(response.getOutputStream());
            response.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
