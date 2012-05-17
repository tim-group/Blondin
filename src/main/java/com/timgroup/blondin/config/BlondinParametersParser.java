package com.timgroup.blondin.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.google.common.base.Optional;

import static java.lang.Integer.parseInt;

public final class BlondinParametersParser {

    private static final String DEFAULT_THROTTLE_BANDWIDTH = "16";
    private static final String DEFAULT_EXPENSIVE_RESOURCES_FILE = "blacklist.txt";
    
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
            
            final int port = parseInt(prop.getProperty("port", defaultPortString));
            final String hostName = InetAddress.getLocalHost().getHostName().replace('.', '_');
            final String identifier = String.format("blondin.%s.%d", hostName, port); 
            
            return Optional.of(new BlondinConfiguration(port,
                                                        prop.getProperty("targetHost").toString(),
                                                        parseInt(prop.getProperty("targetPort")),
                                                        parseUrl(prop.getProperty("expensiveResourcesUrl")),
                                                        parseInt(prop.getProperty("throttleSize", DEFAULT_THROTTLE_BANDWIDTH)),
                                                        new BlondingDiagnosticsConfiguration(identifier,
                                                                                             prop.getProperty("logDirectory", "").toString(),
                                                                                             prop.getProperty("statsd.host", "").toString(),
                                                                                             parseInt(prop.getProperty("statsd.port", "0")),
                                                                                             prop.getProperty("graphite.host", "").toString(),
                                                                                             parseInt(prop.getProperty("graphite.port", "0")),
                                                                                             parseInt(prop.getProperty("graphite.period", "0")),
                                                                                             prop.getProperty("graphite.periodunit", "").toString())));
        } catch (Exception e) {
            return Optional.absent();
        }
    }

    private URL parseUrl(String property) throws IOException {
        return (null == property) ? new File(DEFAULT_EXPENSIVE_RESOURCES_FILE).toURI().toURL() : new URL(property);
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
