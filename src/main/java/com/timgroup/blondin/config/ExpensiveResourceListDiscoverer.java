package com.timgroup.blondin.config;

import java.net.URL;
import java.util.Collections;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public final class ExpensiveResourceListDiscoverer {

    private final URL blackListLocation;
    private Iterable<String> blackList = ImmutableList.of();

    public ExpensiveResourceListDiscoverer(URL blackListLocation) {
        this.blackListLocation = blackListLocation;
        refresh();
    }

    public void refresh() {
        try {
            blackList = Collections.unmodifiableList(Resources.readLines(blackListLocation, Charsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Iterable<String> expensiveResources() {
        return blackList;
    }
}
