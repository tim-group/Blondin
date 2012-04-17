package com.timgroup.blondin.testutil;

import org.junit.After;
import org.junit.Before;

import com.timgroup.blondin.Blondin;

import static java.lang.String.format;

public class BlondinAcceptanceTestBase {

    private static final class BlondinTestContext {
        private static int blondinPort = 23453;
        private static int targetPort = 34297;
    }
    
    @Before
    public final void startBlondin() throws Exception {
        Blondin.main(new String[] {String.valueOf(BlondinTestContext.blondinPort), "localhost", String.valueOf(BlondinTestContext.targetPort)});
        TrivialHttpClient.waitForSocket("localhost", BlondinTestContext.blondinPort);
    }
    
    @After
    public final void stopBlondin() throws Exception {
        TrivialHttpClient.post(format("http://localhost:%s/stop", BlondinTestContext.blondinPort));
        BlondinTestContext.blondinPort++;
        BlondinTestContext.targetPort++;
    }
    
    public final int blondinPort() {
        return BlondinTestContext.blondinPort;
    }
    
    public final String blondinUrl() {
        return format("http://localhost:" + BlondinTestContext.blondinPort);
    }
    
    public final int targetPort() {
        return BlondinTestContext.targetPort;
    }
}
