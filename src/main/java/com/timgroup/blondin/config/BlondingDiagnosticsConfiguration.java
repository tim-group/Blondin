package com.timgroup.blondin.config;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;

public final class BlondingDiagnosticsConfiguration {

    public static final BlondingDiagnosticsConfiguration NO_OP = new BlondingDiagnosticsConfiguration(null, null, 1, 1, null);
    
    private final String logDirectory;

    private final String graphiteHost;
    private final int graphitePort;
    private final int graphitePeriod;
    private final TimeUnit graphitePeriodTimeUnit;

    public BlondingDiagnosticsConfiguration(String logDirectory, String graphiteHost, int graphitePort, int graphitePeriod, String graphitePeriodTimeUnit) {
        this.logDirectory = logDirectory;
        this.graphiteHost = graphiteHost;
        this.graphitePort = graphitePort;
        this.graphitePeriod = graphitePeriod;
        this.graphitePeriodTimeUnit = parseTimeUnit(graphitePeriodTimeUnit);
    }

    private TimeUnit parseTimeUnit(String timeUnit) {
        try {
            return TimeUnit.valueOf(timeUnit);
        }
        catch (Exception e) {
            return TimeUnit.MINUTES;
        }
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
