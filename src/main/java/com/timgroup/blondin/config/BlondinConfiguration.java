package com.timgroup.blondin.config;

import java.net.URL;

public final class BlondinConfiguration {

    private static final int DEFAULT_THROTTLE_BANDWIDTH = 16;
    
    private final int blondinPort;
    private final String targetHost;
    private final int targetPort;
    private final URL expensiveResourcesUrl;
    private final BlondingDiagnosticsConfiguration diagnostics;

    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl) {
        this(blondinPort, targetHost, targetPort, expensiveResourcesUrl, new BlondingDiagnosticsConfiguration(null, null, 1, 1, null));
    }

    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl, BlondingDiagnosticsConfiguration diagnostics) {
        this.blondinPort = blondinPort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.expensiveResourcesUrl = expensiveResourcesUrl;
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
        return DEFAULT_THROTTLE_BANDWIDTH;
    }
}
