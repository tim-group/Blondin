package com.timgroup.blondin;

import com.google.common.base.Supplier;
import com.timgroup.blondin.config.BlondinConfiguration;
import com.timgroup.blondin.config.BlondinParametersParser;
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
        System.out.printf("Starting blondin on port %s targetting %s:%s\n", config.blondinPort(), config.targetHost(), config.targetPort());
        new BlondinServer(config.blondinPort(), config.targetHost(), config.targetPort(), config.expensiveResourcesUrl());
    }
}
