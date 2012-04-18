package com.timgroup.blondin;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class BlondinTest {

    @Test public void
    rejects_zero_argument_startup() {
        try {
            Blondin.main(new String[0]);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Usage: Blondin [port] configfile.properties"));
        }
    }

    @Test public void
    rejects_one_argument_startup_where_first_argument_is_not_a_properties_file() {
        try {
            Blondin.main(new String[] {"bad"});
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Usage: Blondin [port] configfile.properties"));
        }
    }

    @Test public void
    rejects_two_argument_startup_where_first_argument_is_not_an_integer() {
        try {
            Blondin.main(new String[] {"bad", "bad"});
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Usage: Blondin [port] configfile.properties"));
        }
    }

}
