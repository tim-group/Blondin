package com.timgroup.blondin.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
        final File configFile = setupConfigFile("1", "localhost", "2");
        assertThat(parser.parse(new String[] {configFile.getAbsolutePath()}).isPresent(), is(true));
    }

    private File setupConfigFile(String blondinPort, String targetHost, String targetPort) {
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
        try {
            prop.store(new FileOutputStream(configFile), null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return configFile;
    }
}
