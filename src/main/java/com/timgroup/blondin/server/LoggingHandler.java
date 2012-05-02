package com.timgroup.blondin.server;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.blondin.diagnostics.Monitor;

public final class LoggingHandler implements Container {

    private final Container delegate;
    private final Monitor monitor;

    public LoggingHandler(Monitor monitor, Container delegate) {
        this.monitor = monitor;
        this.delegate = delegate;
    }

    @Override
    public void handle(Request req, Response resp) {
        try {
            monitor.logInfo(LoggingHandler.class, req.getPath().getPath());
        }
        catch (Exception e) {
            monitor.logError(LoggingHandler.class, "Failed to log incoming request", e);
        }
        delegate.handle(req, resp);
    }

}
