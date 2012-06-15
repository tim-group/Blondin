package com.timgroup.blondin.config;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Optional;

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
        final File configFile = setupConfigFile("1", "sausage", "2", "http://foo/bar", "10");
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().blondinPort(), is(1));
        assertThat(result.get().targetHost(), is("sausage"));
        assertThat(result.get().targetPort(), is(2));
        assertThat(result.get().expensiveResourcesUrl().toExternalForm(), is("http://foo/bar"));
        assertThat(result.get().throttleSize(), is(10));
    }

    @Test public void
    accepts_two_arguments_when_they_are_a_port_followed_by_a_properties_file() {
        final File configFile = setupConfigFile(null, "sausage", "2", null, "16");
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {"123", configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().blondinPort(), is(123));
        assertThat(result.get().targetHost(), is("sausage"));
        assertThat(result.get().targetPort(), is(2));
        assertThat(result.get().expensiveResourcesUrl().toExternalForm(), endsWith("blacklist.txt"));
        assertThat(result.get().throttleSize(), is(16));
    }

    @Test public void
    port_in_properties_file_overrides_port_argument() {
        final File configFile = setupConfigFile("1", "sausage", "2", null, "5");
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {"123", configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().blondinPort(), is(1));
    }
    
    @Test public void
    identifies_server_by_port_and_host() throws Exception {
        final File configFile = setupConfigFile("1", "sausage", "2", null, "5");
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {"123", configFile.getAbsolutePath()});
        
        final String hostName = InetAddress.getLocalHost().getHostName().replace('.', '_');
        assertThat(result.get().diagnostics().identifier(), is("blondin." + hostName + ".1"));
    }

    @Test public void
    defaults_throttle_size_when_unspecified_in_properties_file() {
        final File configFile = setupConfigFile("1", "sausage", "2", null, null);
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().throttleSize(), is(16));
    }

    @Test public void
    rejects_invalid_blondin_port_in_properties_file() {
        final File configFile = setupConfigFile("x", "sausage", "2", null, "1");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(false));
    }

    @Test public void
    rejects_unspecified_blondin_port_in_both_command_line_and_properties_file() {
        final File configFile = setupConfigFile(null, "sausage", "2", null, "1");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(false));
    }

    @Test public void
    rejects_invalid_target_port_in_properties_file() {
        final File configFile = setupConfigFile("1", "sausage", "y", null, "1");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(false));
    }

    @Test public void
    rejects_invalid_target_host_in_properties_file() {
        final File configFile = setupConfigFile("1", null, "2", null, "1");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(false));
    }

    @Test public void
    rejects_invalid_expensive_resources_url_in_properties_file() {
        final File configFile = setupConfigFile("1", "sausage", "2", "sdg:sdghjwe::sdgx23", "1");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(false));
    }

    @Test public void
    rejects_invalid_throttle_size_in_properties_file() {
        final File configFile = setupConfigFile("1", "sausage", "2", null, "c");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(false));
    }

    @Test public void
    reads_diagnostics_configuration_from_properties_file() {
        final File configFile = setupConfigFile("1", "sausage", "2", "http://foo/bar", "1");
        augmentConfigFileWithDiagnostics(configFile, "/my/log/dir", "my.statsd.host", "3");
        
        final Optional<BlondinConfiguration> result = parser.parse(new String[] {configFile.getAbsolutePath()});
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().diagnostics().logDirectory(), is("/my/log/dir"));
        assertThat(result.get().diagnostics().statsdHost(), is("my.statsd.host"));
        assertThat(result.get().diagnostics().statsdPort(), is(3));
    }

    private File setupConfigFile(String blondinPort, String targetHost, String targetPort, String expensiveResourcesUrl, String throttleSize) {
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
        if (null != throttleSize) { prop.setProperty("throttleSize", throttleSize); }
        try {
            prop.store(new FileOutputStream(configFile), null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return configFile;
    }

    private void augmentConfigFileWithDiagnostics(File configFile, String logDir, String statsdHost, String statsdPort) {
        try {
            final Properties prop = new Properties();
            prop.load(new FileInputStream(configFile));
            
            if (null != logDir) { prop.setProperty("logDirectory", logDir); }
            if (null != statsdHost) { prop.setProperty("statsd.host", statsdHost); }
            if (null != statsdPort) { prop.setProperty("statsd.port", statsdPort); }
            prop.store(new FileOutputStream(configFile), null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
