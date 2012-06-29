package com.timgroup.blondin.server;

import static com.timgroup.blondin.server.RequestDispatcher.GET;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.google.common.base.Predicate;
import com.timgroup.blondin.config.ExpensiveResourceListLoader;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.proxy.ProxyingHandler;
import com.timgroup.blondin.server.handler.AppInfoHandler;
import com.timgroup.blondin.server.handler.LoggingHandler;
import com.timgroup.blondin.server.handler.MetricRecordingHandler;
import com.timgroup.blondin.throttler.ThrottlingHandler;

public final class BlondinServer {

    private static final int THREAD_COUNT = 100;

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    private final Monitor monitor;
    private final Connection connection;

    public BlondinServer(Monitor monitor, int blondinPort, String targetHost, int targetPort,
                         URL expensiveResourcesUrl, int throttleSize) throws IOException
    {
        this.monitor = monitor;
        final RequestDispatcher dispatcher = new RequestDispatcher(monitor);
        
        final ExpensiveResourceListLoader throttleListSupplier = new ExpensiveResourceListLoader(monitor, expensiveResourcesUrl);
        dispatcher.register(GET.forPath(startingWith("/info")), new AppInfoHandler(monitor, throttleListSupplier));
        
        final Container proxy = ProxyingHandler.create(monitor, targetHost, targetPort);
        dispatcher.register(GET.forPath(throttleListSupplier), new MetricRecordingHandler(monitor, "requests.expensive", new ThrottlingHandler(proxy, throttleSize)));
        dispatcher.register(GET, new MetricRecordingHandler(monitor, "requests.normal", proxy));
        
        connection = new SocketConnection(new ContainerServer(new LoggingHandler(monitor, dispatcher), THREAD_COUNT));
        connection.connect(new InetSocketAddress(blondinPort));
    }

    public void stop() {
        try {
            connection.close();
        }
        catch (IOException e) {
            monitor.logWarning(BlondinServer.class, "Failed to stop Blondin server", e);
        }
        monitor.stop();
    }

    private static final Predicate<String> startingWith(final String pathPrefix) {
        return new Predicate<String>(){
            @Override public boolean apply(String path) {
                return path.startsWith(pathPrefix);
            }
        };
    }
}
