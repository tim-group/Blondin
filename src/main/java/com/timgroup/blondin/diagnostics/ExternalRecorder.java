package com.timgroup.blondin.diagnostics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.timgroup.blondin.config.BlondingDiagnosticsConfiguration;

public final class ExternalRecorder implements Monitor {

    private GraphiteRecorder graphite = null;
    private StatsdRecorder statsd = null;
    private ScheduledExecutorService executor = null;

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
    public void logInfo(Class<?> source, String message) {
        LoggerFactory.getLogger(source).info(message);
    }

    @Override
    public void plot(String aspect, Integer value) {
        if (null != graphite) {
            graphite.record(aspect, value);
        }
        if (null != statsd) {
            statsd.record(aspect, value);
        }
    }

    private void turnOnLogging(BlondingDiagnosticsConfiguration diagnostics) {
        final String logFileName = diagnostics.logDirectory() + "/blondin.log";
        try {
            final Logger logger = java.util.logging.Logger.getLogger("");
            logger.setLevel(Level.WARNING);
            logger.addHandler(new FileHandler(logFileName));
        } catch (Exception e) {
            System.err.println("Unable to configure logging to " + logFileName);
            e.printStackTrace();
        }
    }

    private void turnOnMetrics(final BlondingDiagnosticsConfiguration diagnostics) {
        if (!Strings.isNullOrEmpty(diagnostics.statsdHost())) {
            statsd = new StatsdRecorder(this, diagnostics.statsdHost(), diagnostics.statsdPort());
            return;
        }
        
        executor  = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            final ThreadFactory delegate = Executors.defaultThreadFactory();
            @Override public Thread newThread(Runnable r) {
                Thread result = delegate.newThread(r);
                result.setName("Metrics-"+result.getName());
                return result;
            }
        });
        graphite = new GraphiteRecorder(this, diagnostics.graphiteHost(), diagnostics.graphitePort());
        executor .scheduleWithFixedDelay(graphite, diagnostics.graphitePeriod(), diagnostics.graphitePeriod(), diagnostics.graphitePeriodTimeUnit());
    }

    @Override
    public void stop() {
        if (null != executor) {
            executor.shutdown();
        }
    }
}
