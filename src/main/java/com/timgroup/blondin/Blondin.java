package com.timgroup.blondin;

import java.io.IOException;
import java.util.logging.FileHandler;

import com.google.common.base.Supplier;
import com.timgroup.blondin.config.BlondinConfiguration;
import com.timgroup.blondin.config.BlondinParametersParser;
import com.timgroup.blondin.config.BlondingDiagnosticsConfiguration;
import com.timgroup.blondin.diagnostics.ExternalRecorder;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.BlondinServer;
import com.yammer.metrics.reporting.GraphiteReporter;

public final class Blondin {

    private static final Supplier<BlondinConfiguration> USAGE_SUPPLIER = new Supplier<BlondinConfiguration>() {
        @Override
        public BlondinConfiguration get() {
            throw new IllegalArgumentException("Usage: Blondin [port] configfile.properties");
        }
    };

    public static void main(String[] args) {
        final BlondinConfiguration config = new BlondinParametersParser().parse(args).or(USAGE_SUPPLIER);
        turnOnLogging(config.diagnostics());
        turnOnMetrics(config.diagnostics());
        
        final Monitor monitor = new ExternalRecorder();
        
        try {
            System.out.printf("Starting blondin on port %s targetting %s:%s\n", config.blondinPort(), config.targetHost(), config.targetPort());
            new BlondinServer(monitor, config.blondinPort(), config.targetHost(), config.targetPort(), config.expensiveResourcesUrl());
        }
        catch (IOException e) {
            monitor.logError(Blondin.class, "Failed to start Blondin server", e);
            throw new IllegalStateException(e);
        }
    }

    private static void turnOnLogging(BlondingDiagnosticsConfiguration diagnostics) {
        if (diagnostics.loggingEnabled()) {
            final String logFileName = diagnostics.logDirectory() + "/blondin.log";
            try {
                java.util.logging.Logger.getLogger("").addHandler(new FileHandler(logFileName));
            } catch (Exception e) {
                System.err.println("Unable to configure logging to " + logFileName);
                e.printStackTrace();
            }
        }
    }

    private static void turnOnMetrics(final BlondingDiagnosticsConfiguration diagnostics) {
        if (diagnostics.metricsEnabled()) {
            GraphiteReporter.enable(diagnostics.graphitePeriod(), diagnostics.graphitePeriodTimeUnit(), diagnostics.graphiteHost(), diagnostics.graphitePort());
        }
    }
}
