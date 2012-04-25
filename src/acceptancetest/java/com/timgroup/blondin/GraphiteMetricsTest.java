package com.timgroup.blondin;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Test;

import com.timgroup.blondin.testutil.BlondinAcceptanceTestBase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class GraphiteMetricsTest extends BlondinAcceptanceTestBase {

    final DummyGraphiteServer graphite = new DummyGraphiteServer(22222);

    @Override
    protected void beforeBlondinStartsUpWith(Properties properties, List<String> expensiveResources) throws Exception {
        properties.setProperty("graphite.host", "localhost");
        properties.setProperty("graphite.port", "22222");
        properties.setProperty("graphite.period", "1");
        properties.setProperty("graphite.periodunit", "MILLISECONDS");
    }

    @After
    public void shutdownGraphiteServer() {
        graphite.shutdown();
    }
    
    @Test(timeout=5000) public void
    blondin_connects_to_graphite() throws Exception {
        graphite.waitForFirstConnection();
        
        assertThat(graphite.connected(), is(true));
    }

    public static final class DummyGraphiteServer {
        
        private final AtomicBoolean connected = new AtomicBoolean(false);
        private final ServerSocket server;
        
        public DummyGraphiteServer(int port) {
            try {
                server = new ServerSocket(22222);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        server.accept();
                        connected.set(true);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }

            }).start();
        }
        
        public void shutdown() {
            try {
                server.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        
        public void waitForFirstConnection() {
            while (!connected()) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) { }
            }
        }

        public boolean connected() {
            return connected.get();
        }
    }
}
