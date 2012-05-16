package com.timgroup.blondin.diagnostics;


public interface Monitor {

    void stop();

    // Logging
    void logError(Class<?> source, String message, Throwable cause);
    void logWarning(Class<?> source, String message);
    void logWarning(Class<?> source, String message, Throwable cause);
    void logInfo(Class<?> source, String message);

    // Metrics
    void plot(String aspect, Integer value);
}
