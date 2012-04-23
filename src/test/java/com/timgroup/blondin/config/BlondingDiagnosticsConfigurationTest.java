package com.timgroup.blondin.config;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondingDiagnosticsConfigurationTest {

    @Test public void
    reports_logging_enabled_for_valid_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("/my/log/dir", null, 1, 1);
        
        assertThat(configuration.loggingEnabled(), is(true));
    }

    @Test public void
    reports_logging_disabled_for_blank_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, 1, 1);
        
        assertThat(configuration.loggingEnabled(), is(false));
    }

    @Test public void
    reports_logging_disabled_for_null_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration(null, null, 1, 1);
        
        assertThat(configuration.loggingEnabled(), is(false));
    }

}
