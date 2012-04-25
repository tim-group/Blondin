package com.timgroup.blondin.diagnostics;

import com.google.common.base.Supplier;

public interface Diagnostician {

    // Logging
    void logError(Class<?> source, String message, Throwable cause);
    void logWarning(Class<?> source, String message);
    void logWarning(Class<?> source, String message, Throwable cause);

    // Metrics
    void monitor(String aspect, Supplier<Integer> probe);
}
