package com.timgroup.blondin.server;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.blondin.diagnostics.Monitor;

public final class DefensiveHandler implements Container {

    private final Container decoratedHandler;
    private final Monitor monitor;

    public DefensiveHandler(Monitor monitor, Container decoratedHandler) {
        this.monitor = monitor;
        this.decoratedHandler = decoratedHandler;
    }

    @Override
    public void handle(Request req, Response resp) {
        try {
            decoratedHandler.handle(req, resp);
        } catch (Exception e) {
            monitor.logError(DefensiveHandler.class, "Failed to handle request", e);
        } finally {
            try {
                resp.close();
            } catch (IOException e) {
                monitor.logError(DefensiveHandler.class, "Failed to close response", e);
            }
        }
    }

}
