package com.timgroup.blondin.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

import static org.hamcrest.Matchers.is;

import static com.google.common.base.Charsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class ExpensiveResourceListLoaderTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    private File expensiveListFile;
    
    @Before
    public void setup() throws Exception {
        expensiveListFile = testFolder.newFile();
    }

    @Test public void
    reads_blacklist_from_specified_url() throws Exception {
        Files.write("yo\ndawg", expensiveListFile, UTF_8);
        final ExpensiveResourceListLoader loader = new ExpensiveResourceListLoader(urlFor(expensiveListFile));
        
        assertThat(loader.expensiveResources(), contains("yo", "dawg"));
    }

    @Test public void
    calculates_matching_resources() throws Exception {
        Files.write("yo\ndawg", expensiveListFile, UTF_8);
        final ExpensiveResourceListLoader loader = new ExpensiveResourceListLoader(urlFor(expensiveListFile));
        
        assertThat(loader.apply("yo"), is(true));
        assertThat(loader.apply("dawg"), is(true));
        assertThat(loader.apply("yoi"), is(false));
    }

    @Test public void
    matches_resources_using_templates() throws Exception {
        Files.write("yo\n/dawg/{token}/kat", expensiveListFile, UTF_8);
        final ExpensiveResourceListLoader loader = new ExpensiveResourceListLoader(urlFor(expensiveListFile));
        
        assertThat(loader.apply("/dawg/eats/kat"), is(true));
    }

    @Test public void
    matches_resources_containing_regex_sensitive_characters() throws Exception {
        Files.write("/$/{token}/(", expensiveListFile, UTF_8);
        final ExpensiveResourceListLoader loader = new ExpensiveResourceListLoader(urlFor(expensiveListFile));
        
        assertThat(loader.apply("/$/anything/("), is(true));
    }

    private URL urlFor(File file) throws MalformedURLException {
        return file.toURI().toURL();
    }

}
