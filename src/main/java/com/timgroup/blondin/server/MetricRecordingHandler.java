package com.timgroup.blondin.server;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.blondin.diagnostics.Monitor;

public final class MetricRecordingHandler implements Container {

    private final Container delegate;
    private final Monitor monitor;

    public MetricRecordingHandler(Monitor monitor, Container delegate) {
        this.monitor = monitor;
        this.delegate = delegate;
    }

    @Override
    public void handle(Request req, Response resp) {
        monitor.plot("connections.received", 1);
        delegate.handle(req, resp);
    }

}
