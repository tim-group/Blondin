package com.timgroup.blondin.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public final class BlondingDiagnosticsConfigurationTest {

    @Test public void
    reports_logging_enabled_for_valid_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", "/my/log/dir", null, 1);
        
        assertThat(configuration.loggingEnabled(), is(true));
    }

    @Test public void
    reports_logging_disabled_for_blank_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", "", null, 1);
        
        assertThat(configuration.loggingEnabled(), is(false));
    }

    @Test public void
    reports_logging_disabled_for_null_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1);
        
        assertThat(configuration.loggingEnabled(), is(false));
    }

    @Test public void
    reports_metrics_enabled_for_valid_statsd_configuration() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "my.statsd.host", 8125);
        
        assertThat(configuration.metricsEnabled(), is(true));
    }
    
    @Test public void
    reports_metrics_disabled_for_blank_statsd_host() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "", 8125);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_null_statsd_host() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 8125);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_zero_statsd_port() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "my.statsd.host", 0);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_negative_statsd_port() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "my.statsd.host", -1);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }
}
