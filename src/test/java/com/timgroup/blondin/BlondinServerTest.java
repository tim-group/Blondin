package com.timgroup.blondin;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BlondinServerTest {

    @Test(timeout=1000) public void
    reports_startup_success() {
        final BlondinServer blondin = new BlondinServer("x:21", 31415);
        while(!blondin.running()) { }
        assertThat(blondin.running(), is(true));
    }
}