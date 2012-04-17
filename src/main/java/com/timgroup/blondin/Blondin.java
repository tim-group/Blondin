package com.timgroup.blondin;

import java.io.FileInputStream;
import java.util.Properties;

import com.timgroup.blondin.server.BlondinServer;

public final class Blondin {
    private static final String USAGE = "Usage: Blondin [port] configfile.properties";

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException(USAGE);
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
            throw new IllegalArgumentException(USAGE, e);
        }
        
        new BlondinServer(port, targetHost, targetPort);
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
