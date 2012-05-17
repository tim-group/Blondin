package com.timgroup.blondin.server.handler;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.timgroup.blondin.diagnostics.Monitor;

public final class MetricRecordingHandler implements Container {

    private final Monitor monitor;
    private final String label;
    private final Container delegate;

    public MetricRecordingHandler(Monitor monitor, String label, Container delegate) {
        this.monitor = monitor;
        this.label = label;
        this.delegate = delegate;
    }

    @Override
    public void handle(Request req, Response resp) {
        monitor.plot(label, 1);
        delegate.handle(req, resp);
    }

}
