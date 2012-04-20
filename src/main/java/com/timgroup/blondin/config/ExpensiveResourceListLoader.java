package com.timgroup.blondin.config;

import java.net.URL;
import java.util.Collections;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;

public final class ExpensiveResourceListLoader implements Supplier<Iterable<String>>, Predicate<String> {

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

    @Override
    public boolean apply(String path) {
        return any(transform(blackList, toPatterns()), matches(path));
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
                return Pattern.compile(tokenisedPath.replaceAll("\\{.*?\\}", ".*?"));
            }
        };
    }
}
