package com.timgroup.blondin;

import java.io.IOException;

import com.google.common.base.Supplier;
import com.timgroup.blondin.config.BlondinConfiguration;
import com.timgroup.blondin.config.BlondinParametersParser;
import com.timgroup.blondin.diagnostics.ExternalRecorder;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.BlondinServer;

public final class Blondin {

    private static final Supplier<BlondinConfiguration> USAGE_SUPPLIER = new Supplier<BlondinConfiguration>() {
        @Override
        public BlondinConfiguration get() {
            throw new IllegalArgumentException("Usage: Blondin [port] configfile.properties");
        }
    };

    public static void main(String[] args) {
        final BlondinConfiguration config = new BlondinParametersParser().parse(args).or(USAGE_SUPPLIER);
        final Monitor monitor = new ExternalRecorder(config.diagnostics());
        final BlondinServer blondinServer;
        
        try {
            System.out.printf("Starting blondin on port %s targetting %s:%s\n", config.blondinPort(), config.targetHost(), config.targetPort());
            blondinServer = new BlondinServer(monitor, config.blondinPort(), config.targetHost(), config.targetPort(),
                                              config.expensiveResourcesUrl(), config.throttleSize());
        }
        catch (IOException e) {
            monitor.logError(Blondin.class, "Failed to start Blondin server", e);
            throw new IllegalStateException(e);
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override public void run() {
                blondinServer.stop();
            }
        }));
    }
}
