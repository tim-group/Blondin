package com.timgroup.blondin.config;

import java.io.FileInputStream;
import java.util.Properties;

import com.google.common.base.Optional;

public final class BlondinParametersParser {
    
    public Optional<BlondinConfiguration> parse(String[] args) {
        if (args.length == 0) {
            return Optional.absent();
        }

        String propertiesFilename = args[0];
        String defaultPort = "0";
        if (args.length > 1 && isInteger(args[0])) {
            defaultPort = args[0];
            propertiesFilename = args[1];
        }

        final int port;
        final String targetHost;
        final int targetPort;
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(propertiesFilename));
            port = Integer.parseInt(prop.getProperty("port", defaultPort));
            targetHost = prop.getProperty("targetHost").toString();
            targetPort = Integer.parseInt(prop.getProperty("targetPort"));
        } catch (Exception e) {
            return Optional.absent();
        }
        
        return Optional.of(new BlondinConfiguration(port, targetHost, targetPort));
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
