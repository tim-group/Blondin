package com.timgroup.blondin.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondinParametersParserTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private final BlondinParametersParser parser = new BlondinParametersParser();

    @Test public void
    rejects_zero_arguments() {
        assertThat(parser.parse(new String[0]).isPresent(), is(false));
    }

    @Test public void
    rejects_singleton_argument_when_it_is_not_a_properties_file() {
        assertThat(parser.parse(new String[] {"bad"}).isPresent(), is(false));
    }

    @Test public void
    rejects_second_argument_when_first_argument_is_not_an_integer() {
        assertThat(parser.parse(new String[] {"bad", "bad"}).isPresent(), is(false));
    }

    @Test public void
    accepts_single_argument_when_it_is_a_properties_file() {
        final File configFile = setupConfigFile("1", "sausage", "2", "http://foo/bar");
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().blondinPort(), is(1));
        assertThat(result.get().targetHost(), is("sausage"));
        assertThat(result.get().targetPort(), is(2));
        assertThat(result.get().expensiveResourcesUrl().toExternalForm(), is("http://foo/bar"));
    }

    @Test public void
    accepts_two_arguments_when_they_are_a_port_followed_by_a_properties_file() {
        final File configFile = setupConfigFile(null, "sausage", "2", null);
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {"123", configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().blondinPort(), is(123));
        assertThat(result.get().targetHost(), is("sausage"));
        assertThat(result.get().targetPort(), is(2));
        assertThat(result.get().expensiveResourcesUrl().toExternalForm(), is("file:/./blacklist.txt"));
    }

    @Test public void
    port_in_properties_file_overrides_port_argument() {
        final File configFile = setupConfigFile("1", "sausage", "2", null);
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {"123", configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().blondinPort(), is(1));
        assertThat(result.get().targetHost(), is("sausage"));
        assertThat(result.get().targetPort(), is(2));
    }

    @Test public void
    rejects_invalid_properties_file() {
        final File configFile1 = setupConfigFile("x", "sausage", "2", null);
        assertThat(parser.parse(new String[] {configFile1.getAbsolutePath()}).isPresent(), is(false));
        
        final File configFile2 = setupConfigFile("1", "sausage", "y", null);
        assertThat(parser.parse(new String[] {configFile2.getAbsolutePath()}).isPresent(), is(false));
        
        final File configFile3 = setupConfigFile("1", null, "2", null);
        assertThat(parser.parse(new String[] {configFile3.getAbsolutePath()}).isPresent(), is(false));

        final File configFile4 = setupConfigFile(null, "sausage", "2", null);
        assertThat(parser.parse(new String[] {configFile4.getAbsolutePath()}).isPresent(), is(false));
    }
    
    private File setupConfigFile(String blondinPort, String targetHost, String targetPort, String expensiveResourcesUrl) {
        final File configFile;
        try {
            configFile = testFolder.newFile("blondinconf.properties");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        final Properties prop = new Properties();
        if (null != blondinPort) { prop.setProperty("port", blondinPort); }
        if (null != targetHost) { prop.setProperty("targetHost", targetHost); }
        if (null != targetPort) { prop.setProperty("targetPort", targetPort); }
        if (null != expensiveResourcesUrl) { prop.setProperty("expensiveResourcesUrl", expensiveResourcesUrl); }
        try {
            prop.store(new FileOutputStream(configFile), null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return configFile;
    }
}
