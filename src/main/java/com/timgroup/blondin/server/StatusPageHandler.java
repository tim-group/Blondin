package com.timgroup.blondin.server;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.google.common.base.Supplier;
import com.timgroup.status.StatusPage;

import static java.net.HttpURLConnection.HTTP_NOT_AUTHORITATIVE;

public final class StatusPageHandler implements Container {

    private final StatusPage statusPage = new StatusPage("Blondin");
    private final Supplier<BlondinServerStatus> serverStatusSupplier;
    
    public StatusPageHandler(Supplier<BlondinServerStatus> serverStatusSupplier) {
        this.serverStatusSupplier = serverStatusSupplier;
    }
    
    @Override
    public void handle(Request request, Response response) {
        if (BlondinServerStatus.SUSPENDED.equals(serverStatusSupplier.get())) {
            response.setCode(HTTP_NOT_AUTHORITATIVE);
            response.setText("Non-Authoritative Information");
        }
        
        response.set("Content-Type", "text/xml+status");
        try {
            statusPage.render(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
            response.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
