package com.timgroup.blondin.server.status;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.timgroup.blondin.server.AppInfoHandler;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public final class BlondinStatusTest {

    private final OutputStream responseContent = new ByteArrayOutputStream();
    private final List<String> blackList = Lists.newArrayList();
    private final BlondinStatus status = new BlondinStatus(Suppliers.<Iterable<String>>ofInstance(blackList));
    
    @Test public void
    includes_version_information_in_status_page() throws Exception {
        final String currentVersion = AppInfoHandler.class.getPackage().getImplementationVersion();
        final String expectedVersionString = (null == currentVersion) ? "" : ": <value>" + currentVersion + "</value>";

        status.writeTo(responseContent);
        assertThat(responseContent.toString(), containsString("Version" + expectedVersionString));
    }
    
    @Test public void
    includes_expensive_resource_information_in_status_page() throws Exception {
        blackList.add("great/big/{yellow}/banana");
        blackList.add("little/round/{green}/apple");
        status.writeTo(responseContent);
        assertThat(responseContent.toString(), containsString("Throttled Resources: <value>great/big/{yellow}/banana;little/round/{green}/apple</value>"));
    }
}
