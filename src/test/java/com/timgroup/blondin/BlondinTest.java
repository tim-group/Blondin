package com.timgroup.blondin;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class BlondinTest {

    @Test public void
    fails_and_reports_usage_when_started_with_invalid_arguments() {
        try {
            Blondin.main(new String[0]);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Usage: Blondin [port] configfile.properties"));
        }
    }
}
