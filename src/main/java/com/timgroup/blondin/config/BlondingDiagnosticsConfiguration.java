package com.timgroup.blondin.config;

import com.google.common.base.Strings;

public final class BlondingDiagnosticsConfiguration {

    public static final BlondingDiagnosticsConfiguration NO_OP = new BlondingDiagnosticsConfiguration("", null, null, 1);
    
    private final String identifier;
    private final String logDirectory;

    private final String statsdHost;
    private final int statsdPort;

    public BlondingDiagnosticsConfiguration(String identifier, String logDirectory, String statsdHost, int statsdPort) {
        this.identifier = identifier;
        this.logDirectory = logDirectory;
        this.statsdHost = statsdHost;
        this.statsdPort = statsdPort;
    }

    public String identifier() {
        return identifier;
    }

    public String logDirectory() {
        return logDirectory;
    }

    public String statsdHost() {
        return statsdHost;
    }

    public int statsdPort() {
        return statsdPort;
    }

    public boolean loggingEnabled() {
        return !Strings.isNullOrEmpty(logDirectory);
    }

    public boolean metricsEnabled() {
        return (!(Strings.isNullOrEmpty(statsdHost)) && statsdPort > 0);
    }
}
