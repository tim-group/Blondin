package com.timgroup.blondin.config;

public final class BlondinConfiguration {

    private final int blondinPort;
    private final String targetHost;
    private final int targetPort;
    
    public BlondinConfiguration(int blondinPort, String targetHost, int targetPort) {
        this.blondinPort = blondinPort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
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
}
