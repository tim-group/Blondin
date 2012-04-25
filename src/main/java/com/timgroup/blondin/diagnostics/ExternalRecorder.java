package com.timgroup.blondin.diagnostics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.FileHandler;

import org.slf4j.LoggerFactory;

import com.timgroup.blondin.config.BlondingDiagnosticsConfiguration;

public final class ExternalRecorder implements Monitor {

    private GraphiteRecorder recorder = null;

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
        if (null != recorder) {
            recorder.record(aspect, value);
        }
    }

    private void turnOnLogging(BlondingDiagnosticsConfiguration diagnostics) {
        final String logFileName = diagnostics.logDirectory() + "/blondin.log";
        try {
            java.util.logging.Logger.getLogger("").addHandler(new FileHandler(logFileName));
        } catch (Exception e) {
            System.err.println("Unable to configure logging to " + logFileName);
            e.printStackTrace();
        }
    }

    private void turnOnMetrics(final BlondingDiagnosticsConfiguration diagnostics) {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(new GraphiteRecorder(this, diagnostics.graphiteHost(), diagnostics.graphitePort()),
                                        diagnostics.graphitePeriod(), diagnostics.graphitePeriod(),
                                        diagnostics.graphitePeriodTimeUnit());
    }
}
