package com.timgroup.blondin.server;

import static com.timgroup.blondin.server.RequestDispatcher.GET;
import static com.timgroup.blondin.server.RequestDispatcher.POST;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
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
    private volatile BlondinServerStatus status = BlondinServerStatus.STOPPED;

    private final Supplier<BlondinServerStatus> statusSupplier = new Supplier<BlondinServerStatus>() {
        @Override public BlondinServerStatus get() {
            return status;
        }
    };

    public BlondinServer(Monitor monitor, int blondinPort, String targetHost, int targetPort,
                         URL expensiveResourcesUrl, int throttleSize) throws IOException
    {
        this.monitor = monitor;

        final RequestDispatcher dispatcher = new RequestDispatcher(monitor);
        dispatcher.register(POST.forPath("/stop"), new StopHandler());
        dispatcher.register(POST.forPath("/suspend"), new SuspendHandler());
        
        final ExpensiveResourceListLoader throttleListSupplier = new ExpensiveResourceListLoader(monitor, expensiveResourcesUrl);
        final AppInfoHandler appInfoHandler = new AppInfoHandler(monitor, statusSupplier, throttleListSupplier);
        dispatcher.register(GET.forPath(startingWith("/info")), appInfoHandler);
        dispatcher.register(GET.forPath(startingWith("/status")), appInfoHandler);
        
        final Container proxy = ProxyingHandler.create(monitor, targetHost, targetPort);
        dispatcher.register(GET.forPath(throttleListSupplier), new MetricRecordingHandler(monitor, "requests.expensive", new ThrottlingHandler(proxy, throttleSize)));
        dispatcher.register(GET, new MetricRecordingHandler(monitor, "requests.normal", proxy));
        
        connection = new SocketConnection(new ContainerServer(new LoggingHandler(monitor, dispatcher), THREAD_COUNT));
        connection.connect(new InetSocketAddress(blondinPort));
        status = BlondinServerStatus.RUNNING;
    }

    public BlondinServerStatus status() {
        return status;
    }

    public void stop() {
        try {
            connection.close();
        }
        catch (IOException e) {
            monitor.logWarning(BlondinServer.class, "Failed to stop Blondin server", e);
        }
        monitor.stop();
        status = BlondinServerStatus.STOPPED;
    }

    public void suspend() {
        status = BlondinServerStatus.SUSPENDED;
    }

    private final class SuspendHandler implements Container {
        @Override public void handle(Request request, Response response) {
            closeSafely(response);
            suspend();
        }
    }

    private final class StopHandler implements Container {
        @Override public void handle(Request request, Response response) {
            closeSafely(response);
            stop();
        }
    }

    private void closeSafely(Response response) {
        try {
            response.close();
        }
        catch (Exception e) {
            monitor.logWarning(BlondinServer.class, "Failed to close response", e);
        }
    }

    private static final Predicate<String> startingWith(final String pathPrefix) {
        return new Predicate<String>(){
            @Override public boolean apply(String path) {
                return path.startsWith(pathPrefix);
            }
        };
    }
}
