package com.timgroup.blondin.diagnostics;

import java.util.logging.FileHandler;

import org.slf4j.LoggerFactory;

import com.timgroup.blondin.config.BlondingDiagnosticsConfiguration;
import com.yammer.metrics.reporting.GraphiteReporter;

public final class ExternalRecorder implements Monitor {

    public ExternalRecorder(BlondingDiagnosticsConfiguration configuration) {
        if (configuration.loggingEnabled()) {
            turnOnLogging(configuration);
        }
        if (configuration.metricsEnabled()) {
            turnOnMetrics(configuration);
        }
    }

    @Override
    public void logError(Class<?> source, String message, Throwable cause) {
        LoggerFactory.getLogger(source).error(message, cause);
    }

    @Override
    public void logWarning(Class<?> source, String message) {
        LoggerFactory.getLogger(source).warn(message);
    }

    @Override
    public void logWarning(Class<?> source, String message, Throwable cause) {
        LoggerFactory.getLogger(source).warn(message, cause);
    }

    @Override
    public void plot(String aspect, Integer value) {
    }

    private static void turnOnLogging(BlondingDiagnosticsConfiguration diagnostics) {
        final String logFileName = diagnostics.logDirectory() + "/blondin.log";
        try {
            java.util.logging.Logger.getLogger("").addHandler(new FileHandler(logFileName));
        } catch (Exception e) {
            System.err.println("Unable to configure logging to " + logFileName);
            e.printStackTrace();
        }
    }

    private static void turnOnMetrics(final BlondingDiagnosticsConfiguration diagnostics) {
        GraphiteReporter.enable(diagnostics.graphitePeriod(), diagnostics.graphitePeriodTimeUnit(), diagnostics.graphiteHost(), diagnostics.graphitePort());
    }

}
