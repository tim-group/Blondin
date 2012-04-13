package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import static com.google.common.collect.Iterables.find;

public final class RequestDispatcher implements Container {

    private static final Handler DEFAULT_HANDLER = new Handler(null, null, new Container() {
        @Override public void handle(Request request, Response response) {
            try {
                response.setCode(HttpURLConnection.HTTP_NOT_FOUND);
                response.setText("Not Found");
                response.close();
            } catch (IOException e) {
                
            }
        }
    });

    private final List<Handler> handlers = Lists.newArrayList();

    @Override
    public void handle(Request request, Response response) {
        find(handlers, Handler.understanding(request), DEFAULT_HANDLER).handle(request, response);
    }

    public void register(String method, Container container) {
        register(method, null, container);
    }

    public void register(String method, String path, Container container) {
        handlers.add(new Handler(method, path, container));
    }

    private static final class Handler {
        private final String method;
        private final String path;
        private final Container container;

        public Handler(String method, String path, Container container) {
            this.method = method;
            this.path = path;
            this.container = container;
        }

        public boolean canHandle(Request request) {
            return this.method.equals(request.getMethod()) && (this.path == null || this.path.equals(request.getPath().getPath()));
        }

        public void handle(Request request, Response response) {
            this.container.handle(request, response);
        }
        
        public static Predicate<Handler> understanding(final Request request) {
            return new Predicate<Handler>() {
                @Override public boolean apply(Handler handler) {
                    return handler.canHandle(request);
                }
            };
        }
    }
}
