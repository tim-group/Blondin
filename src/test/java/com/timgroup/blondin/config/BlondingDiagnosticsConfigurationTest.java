package com.timgroup.blondin.config;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondingDiagnosticsConfigurationTest {

    @Test public void
    reports_logging_enabled_for_valid_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", "/my/log/dir", null, 1, null, 1, 1, null);
        
        assertThat(configuration.loggingEnabled(), is(true));
    }

    @Test public void
    reports_logging_disabled_for_blank_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", "", null, 1, null, 1, 1, null);
        
        assertThat(configuration.loggingEnabled(), is(false));
    }

    @Test public void
    reports_logging_disabled_for_null_log_directory() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, null, 1, 1, null);
        
        assertThat(configuration.loggingEnabled(), is(false));
    }

    @Test public void
    reports_metrics_enabled_for_valid_statsd_configuration() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "my.statsd.host", 8125, null, 1, 1, null);
        
        assertThat(configuration.metricsEnabled(), is(true));
    }
    
    @Test public void
    reports_metrics_disabled_for_blank_statsd_host() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "", 8125, null, 1, 1, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_null_statsd_host() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 8125, null, 1, 1, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_zero_statsd_port() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "my.statsd.host", 0, null, 1, 1, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_negative_statsd_port() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, "my.statsd.host", -1, null, 1, 1, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }
    
    @Test public void
    reports_metrics_enabled_for_valid_graphite_configuration() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", 2003, 10, null);
        
        assertThat(configuration.metricsEnabled(), is(true));
    }

    @Test public void
    decodes_graphite_polling_period_unit() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", 2003, 10, "MICROSECONDS");
        
        assertThat(configuration.graphitePeriodTimeUnit(), is(TimeUnit.MICROSECONDS));
    }

    @Test public void
    defaults_to_minutes_for_graphite_polling_period_unit() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", 2003, 10, "BANANA");
        
        assertThat(configuration.graphitePeriodTimeUnit(), is(TimeUnit.MINUTES));
    }

    @Test public void
    reports_metrics_disabled_for_blank_graphite_host() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "", 2003, 10, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_null_graphite_host() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, null, 2003, 10, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_zero_graphite_port() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", 0, 10, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_negative_graphite_port() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", -1, 10, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }
    
    @Test public void
    reports_metrics_disabled_for_zero_graphite_period() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", 2003, 0, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }

    @Test public void
    reports_metrics_disabled_for_negative_graphite_period() {
        final BlondingDiagnosticsConfiguration configuration = new BlondingDiagnosticsConfiguration("", null, null, 1, "my.graphite.host", 2003, -1, null);
        
        assertThat(configuration.metricsEnabled(), is(false));
    }
}
