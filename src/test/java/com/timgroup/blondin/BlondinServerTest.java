package com.timgroup.blondin;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondinServerTest {

    @Test public void
    reports_startup_port() {
        assertThat(new BlondinServer("", 31415).port(), is(31415));
    }

}