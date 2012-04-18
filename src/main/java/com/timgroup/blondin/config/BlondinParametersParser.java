package com.timgroup.blondin.config;

import java.io.FileInputStream;
import java.util.Properties;

import com.google.common.base.Optional;

import static java.lang.Integer.parseInt;

public final class BlondinParametersParser {

    public Optional<BlondinConfiguration> parse(String[] args) {
        if (args.length == 0) {
            return Optional.absent();
        }

        String propertiesFilename = args[0];
        String defaultPortString = null;
        if (args.length > 1 && isInteger(args[0])) {
            defaultPortString = args[0];
            propertiesFilename = args[1];
        }

        try {
            final Properties prop = new Properties();
            prop.load(new FileInputStream(propertiesFilename));
            return Optional.of(new BlondinConfiguration(parseInt(prop.getProperty("port", defaultPortString)),
                                                        prop.getProperty("targetHost").toString(),
                                                        parseInt(prop.getProperty("targetPort"))));
        } catch (Exception e) {
            return Optional.absent();
        }
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
