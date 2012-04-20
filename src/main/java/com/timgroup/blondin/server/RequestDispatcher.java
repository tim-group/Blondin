package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import static com.google.common.collect.Iterables.find;

public final class RequestDispatcher implements Container {

    private static final Handler DEFAULT_HANDLER = new Handler(null, new Container() {
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
        register(requestForMethod(method), container);
    }

    public void register(final String method, final String path, Container container) {
        register(Predicates.<Request>and(requestForMethod(method), requestForPath(path)), container);
    }

    public void register(Predicate<Request> condition, Container container) {
        handlers.add(new Handler(condition, container));
    }

    public static Predicate<Request> requestForMethod(final String method) {
        return new Predicate<Request>() {
            @Override public boolean apply(Request request) {
                return method.equals(request.getMethod());
            }
        };
    }

    public static Predicate<Request> requestForPath(final String path) {
        return new Predicate<Request>() {
            @Override public boolean apply(Request request) {
                return  path.equals(request.getPath().getPath());
            }
        };
    }

    private static final class Handler {
        private final Container container;
        private final Predicate<Request> predicate;

        public Handler(Predicate<Request> condition, Container container) {
            this.predicate = condition;
            this.container = container;
        }

        public void handle(Request request, Response response) {
            this.container.handle(request, response);
        }
        
        public static Predicate<Handler> understanding(final Request request) {
            return new Predicate<Handler>() {
                @Override public boolean apply(Handler handler) {
                    return handler.predicate.apply(request);
                }
            };
        }
    }
}
