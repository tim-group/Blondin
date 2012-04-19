package com.timgroup.blondin.config;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

import static com.google.common.base.Charsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class ExpensiveResourceListDiscovererTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    private File blackListFile;
    private URL blacklistUrl;
    
    @Before
    public void setup() throws Exception {
        blackListFile = testFolder.newFile();
        blacklistUrl = blackListFile.toURI().toURL();
    }
    
    @Test public void
    reads_blacklist_from_specified_url() throws Exception {
        Files.write("yo\ndawg", blackListFile, UTF_8);
        final ExpensiveResourceListDiscoverer discoverer = new ExpensiveResourceListDiscoverer(blacklistUrl);
        
        assertThat(discoverer.expensiveResources(), contains("yo", "dawg"));
    }
}
