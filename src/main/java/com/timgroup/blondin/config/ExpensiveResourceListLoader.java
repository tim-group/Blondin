package com.timgroup.blondin.config;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.timgroup.blondin.diagnostics.Monitor;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterators.forArray;
import static com.google.common.collect.Iterators.transform;

public final class ExpensiveResourceListLoader implements Supplier<Iterable<String>>, Predicate<String> {

    private final Monitor monitor;
    private final URL blackListLocation;

    private Iterable<String> blackList = ImmutableList.of();
    private Iterable<Pattern> blackListPatterns = ImmutableList.of();

    public ExpensiveResourceListLoader(Monitor monitor, URL blackListLocation) {
        this.monitor = monitor;
        this.blackListLocation = blackListLocation;
        refresh();
    }

    public void refresh() {
        try {
            blackList = Collections.unmodifiableList(Resources.readLines(blackListLocation, Charsets.UTF_8));
            blackListPatterns = ImmutableList.copyOf(transform(blackList, toPatterns()));
        } catch (Exception e) {
            monitor.logError(ExpensiveResourceListLoader.class, "Failed to read expensive resources list from " + blackListLocation, e);
        }
    }

    public Iterable<String> expensiveResources() {
        return blackList;
    }

    @Override
    public Iterable<String> get() {
        return expensiveResources();
    }

    @Override
    public boolean apply(String path) {
        return any(blackListPatterns, matches(path));
    }

    private Predicate<Pattern> matches(final String path) {
        return new Predicate<Pattern>() {
            @Override public boolean apply(Pattern pattern) {
                return pattern.matcher(path).matches();
            }
        };
    }

    private Function<String, Pattern> toPatterns() {
        return new Function<String, Pattern>() {
            @Override public Pattern apply(String tokenisedPath) {
                final String[] rawPathComponents = tokenisedPath.split("\\{.*?\\}");
                final Iterator<String> quotedPathComponents = transform(forArray(rawPathComponents), toQuoted());
                final String pathRegex = Joiner.on(".*").join(quotedPathComponents);
                return Pattern.compile(pathRegex);
            }
        };
    }

    private Function<String, String> toQuoted() {
        return new Function<String, String>() {
            @Override public String apply(String input) {
                return Pattern.quote(input);
            }
        };
    }
}
