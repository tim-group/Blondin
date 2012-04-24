package com.timgroup.blondin.config;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;

public final class BlondingDiagnosticsConfiguration {

    private final String logDirectory;

    private final String graphiteHost;
    private final int graphitePort;
    private final int graphitePeriod;
    private final TimeUnit graphitePeriodTimeUnit;

    public BlondingDiagnosticsConfiguration(String logDirectory, String graphiteHost, int graphitePort, int graphitePeriodMinutes) {
        this.logDirectory = logDirectory;
        this.graphiteHost = graphiteHost;
        this.graphitePort = graphitePort;
        this.graphitePeriod = graphitePeriodMinutes;
        this.graphitePeriodTimeUnit = TimeUnit.MINUTES;
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

    public int graphitePeriod() {
        return graphitePeriod;
    }
    
    public TimeUnit graphitePeriodTimeUnit() {
        return graphitePeriodTimeUnit;
    }

    public boolean loggingEnabled() {
        return !Strings.isNullOrEmpty(logDirectory);
    }

    public boolean metricsEnabled() {
        return !(Strings.isNullOrEmpty(graphiteHost)) && graphitePort > 0 && graphitePeriod > 0;
    }
}
