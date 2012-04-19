package com.timgroup.blondin.server.status;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.timgroup.blondin.server.StatusPageHandler;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public final class BlondinStatusTest {

    private final OutputStream responseContent = new ByteArrayOutputStream();
    private final BlondinStatus status = new BlondinStatus();
    
    @Test public void
    includes_version_information_in_status_page() throws Exception {
        final String currentVersion = StatusPageHandler.class.getPackage().getImplementationVersion();
        status.writeTo(responseContent);
        assertThat(responseContent.toString(), containsString("Version: <value>" + currentVersion + "</value>"));
    }

}
