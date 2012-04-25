package com.timgroup.blondin.diagnostics;


public interface Monitor {

    // Logging
    void logError(Class<?> source, String message, Throwable cause);
    void logWarning(Class<?> source, String message);
    void logWarning(Class<?> source, String message, Throwable cause);

    // Metrics
    void plot(String aspect, Integer value);
}
