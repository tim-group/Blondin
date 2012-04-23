package com.timgroup.blondin.config;

import com.google.common.base.Strings;

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

    public boolean loggingEnabled() {
        return !Strings.isNullOrEmpty(logDirectory);
    }

    public boolean metricsEnabled() {
        return !(Strings.isNullOrEmpty(graphiteHost)) && graphitePort > 0 && graphitePeriodMinutes > 0;
    }
}
