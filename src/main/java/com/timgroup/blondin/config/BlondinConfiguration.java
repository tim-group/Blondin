package com.timgroup.blondin.config;

import java.net.URL;

public final class BlondinConfiguration {

    private final int blondinPort;
    private final String targetHost;
    private final int targetPort;
    private final URL expensiveResourcesUrl;
    
    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl) {
        this.blondinPort = blondinPort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.expensiveResourcesUrl = expensiveResourcesUrl;
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
}
