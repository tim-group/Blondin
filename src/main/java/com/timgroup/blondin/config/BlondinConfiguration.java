package com.timgroup.blondin.config;

public final class BlondinConfiguration {

    private final int blondinPort;
    private final String targetHost;
    private final int targetPort;
    private final String expensiveResourcesUrl;
    
    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort, String expensiveResourcesUrl) {
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
    
    public String expensiveResourcesUrl() {
        return expensiveResourcesUrl;
    }
}
