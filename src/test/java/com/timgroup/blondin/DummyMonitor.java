package com.timgroup.blondin;

import com.timgroup.blondin.diagnostics.Monitor;

public final class DummyMonitor implements Monitor {

    @Override
    public void logError(Class<?> source, String message, Throwable cause) {
    }

    @Override
    public void logWarning(Class<?> source, String message) {
    }

    @Override
    public void logWarning(Class<?> source, String message, Throwable cause) {
    }

    @Override
    public void logInfo(Class<?> source, String message) {
    }

    @Override
    public void plot(String aspect, Integer value) {
    }

}
