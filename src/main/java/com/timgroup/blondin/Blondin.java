package com.timgroup.blondin;

import com.timgroup.blondin.server.BlondinServer;

public final class Blondin {

    public static void main(String[] args) {
        new BlondinServer(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
    }

}
