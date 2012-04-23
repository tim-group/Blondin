package com.timgroup.blondin.config;

public final class BlondingDiagnosticsConfiguration {

    private final String logDirectory;

    private final String graphiteHost;
    private final int graphitePort;
    private final int graphitePeriodMinutes;

    public BlondingDiagnosticsConfiguration(String logDirectory, String graphiteHost, int graphitePort, int graphitePeriodMinutes) {
        this.logDirectory = logDirectory;
        this.graphiteHost = graphiteHost;
        this.graphitePort = graphitePort;
        this.graphitePeriodMinutes = graphitePeriodMinutes;
    }

    public String logDirectory() {
        return logDirectory;
    }

    public String graphiteHost() {
        return graphiteHost;
    }

    public int graphitePort() {
        return graphitePort;
    }

    public int graphitePeriodMinutes() {
        return graphitePeriodMinutes;
    }
}
