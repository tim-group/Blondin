package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.timgroup.blondin.config.ExpensiveResourceListLoader;
import com.timgroup.blondin.proxy.BasicHttpClient;
import com.timgroup.blondin.proxy.ProxyingHandler;
import com.timgroup.blondin.throttler.ThrottlingHandler;

import static com.timgroup.blondin.server.RequestDispatcher.GET;

import static com.timgroup.blondin.server.RequestDispatcher.POST;

public final class BlondinServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlondinServer.class);

    private static final int THREAD_COUNT = 100;
    private static final int THROTTLE_BANDWIDTH = 16;

    private final Connection connection;

    private volatile BlondinServerStatus status = BlondinServerStatus.STOPPED;

    private final Supplier<BlondinServerStatus> statusSupplier = new Supplier<BlondinServerStatus>() {
        @Override public BlondinServerStatus get() {
            return status;
        }
    };

    public BlondinServer(int blondinPort, String targetHost, int targetPort, URL expensiveResourcesUrl) throws IOException {
        final ExpensiveResourceListLoader expensiveResourcesListSupplier = new ExpensiveResourceListLoader(expensiveResourcesUrl);

        final RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.register(POST.forPath("/stop"), new StopHandler());
        dispatcher.register(POST.forPath("/suspend"), new SuspendHandler());
        dispatcher.register(GET.forPath(startingWith("/status")), new StatusPageHandler(statusSupplier, expensiveResourcesListSupplier));
        
        final ProxyingHandler proxy = new ProxyingHandler(targetHost, targetPort, new BasicHttpClient());
        dispatcher.register(GET.forPath(expensiveResourcesListSupplier), new ThrottlingHandler(proxy, THROTTLE_BANDWIDTH));
        dispatcher.register(GET, proxy);
        
        connection = new SocketConnection(new ContainerServer(dispatcher, THREAD_COUNT));
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
            LOGGER.warn("Failed to stop Blondin server", e);
        }
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

    private static void closeSafely(Response response) {
        try {
            response.close();
        }
        catch (Exception e) {
            LOGGER.warn("Failed to close response", e);
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
