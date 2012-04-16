package com.timgroup.blondin.server;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.status.StatusPage;

public final class StatusPageHandler implements Container {

    private final StatusPage statusPage = new StatusPage("Blondin");
    
    @Override
    public void handle(Request request, Response response) {
        response.set("Content-Type", "text/xml+status");
        try {
            statusPage.render(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
            response.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
