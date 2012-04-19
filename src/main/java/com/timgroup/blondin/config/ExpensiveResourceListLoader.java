package com.timgroup.blondin.config;

import java.net.URL;
import java.util.Collections;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public final class ExpensiveResourceListLoader implements Supplier<Iterable<String>> {

    private final URL blackListLocation;
    private Iterable<String> blackList = ImmutableList.of();

    public ExpensiveResourceListLoader(URL blackListLocation) {
        this.blackListLocation = blackListLocation;
        refresh();
    }

    public void refresh() {
        try {
            blackList = Collections.unmodifiableList(Resources.readLines(blackListLocation, Charsets.UTF_8));
        } catch (Exception e) { }
    }

    public Iterable<String> expensiveResources() {
        return blackList;
    }

    @Override
    public Iterable<String> get() {
        return expensiveResources();
    }
}
