package com.timgroup.blondin;

import com.timgroup.blondin.server.BlondinServer;

public final class Blondin {

    public static void main(String[] args) {
        final int port = Integer.parseInt(args[0]);
        
        String targetHost = "localhost";
        int targetPort = 80;
        if (args.length == 3) {
            targetHost = args[1];
            targetPort = Integer.parseInt(args[2]);
        }
        
        new BlondinServer(port, targetHost, targetPort);
    }
}
