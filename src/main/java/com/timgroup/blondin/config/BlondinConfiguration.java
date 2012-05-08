package com.timgroup.blondin.config;

import java.net.URL;

public final class BlondinConfiguration {

    private final int blondinPort;
    private final String targetHost;
    private final int targetPort;
    private final URL expensiveResourcesUrl;
    private final int throttleSize;
    private final BlondingDiagnosticsConfiguration diagnostics;

    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl, int throttleSize) {
        this(blondinPort, targetHost, targetPort, expensiveResourcesUrl, throttleSize, BlondingDiagnosticsConfiguration.NO_OP);
    }

    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl, int throttleSize, BlondingDiagnosticsConfiguration diagnostics) {
        this.blondinPort = blondinPort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.expensiveResourcesUrl = expensiveResourcesUrl;
        this.throttleSize = throttleSize;
        this.diagnostics = diagnostics;
    }

    public int blondinPort() {
        return blondinPort;
    }

    public String targetHost() {
        return targetHost;
    }

    public int targetPort() {
        return targetPort;
    }

    public URL expensiveResourcesUrl() {
        return expensiveResourcesUrl;
    }

    public BlondingDiagnosticsConfiguration diagnostics() {
        return diagnostics;
    }

    public int throttleSize() {
        return this.throttleSize;
    }
}
