package com.timgroup.blondin.diagnostics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.timgroup.blondin.config.BlondingDiagnosticsConfiguration;

public final class ExternalRecorder implements Monitor {

    private StatsdRecorder statsd = null;

    public ExternalRecorder(BlondingDiagnosticsConfiguration configuration) {
        Logger globalLogger = Logger.getLogger("");
        for (Handler handler : globalLogger.getHandlers()) {
            globalLogger.removeHandler(handler);
        }
        
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
        if (null != statsd) {
            statsd.record(aspect, value);
        }
    }

    private void turnOnLogging(BlondingDiagnosticsConfiguration diagnostics) {
        final String logFileName = diagnostics.logDirectory() + "/blondin.log";
        try {
            final FileHandler handler = new FileHandler(logFileName);
            handler.setFormatter(new LogMessageFormatter());
            
            final Logger logger = java.util.logging.Logger.getLogger("");
            logger.setLevel(Level.WARNING);
            logger.addHandler(handler);
        } catch (Exception e) {
            System.err.println("Unable to configure logging to " + logFileName);
            e.printStackTrace();
        }
    }

    private void turnOnMetrics(final BlondingDiagnosticsConfiguration diagnostics) {
        if (!Strings.isNullOrEmpty(diagnostics.statsdHost())) {
            statsd = new StatsdRecorder(this, diagnostics.identifier(), diagnostics.statsdHost(), diagnostics.statsdPort());
            return;
        }
    }

    @Override
    public void stop() {
        if (null != statsd) {
            statsd.stop();
        }
    }

    private static final class LogMessageFormatter extends Formatter {
        private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        private Date dat = new Date();

        @Override
        public synchronized String format(LogRecord record) {
            final StringBuffer sb = new StringBuffer();
            dat.setTime(record.getMillis());
            format.format(dat);
            sb.append(format.format(dat));
            sb.append(" ");
            sb.append(record.getLevel().getLocalizedName());
            sb.append(" ");
            sb.append(record.getLoggerName());
            sb.append(": ");
            sb.append(formatMessage(record));
            sb.append("\n");
            if (record.getThrown() != null) {
                sb.append(" ");
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                }
            }
            return sb.toString();
        }
    }
}
