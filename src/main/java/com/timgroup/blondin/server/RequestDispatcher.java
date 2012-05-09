package com.timgroup.blondin.server;

import java.io.IOException;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.timgroup.blondin.diagnostics.Monitor;

import static com.google.common.collect.Iterables.find;

public final class RequestDispatcher implements Container {

    public static final RequestPredicate GET = new RequestPredicate("GET");
    public static final RequestPredicate POST = new RequestPredicate("POST");

    private final Monitor monitor;
    private final List<Handler> handlers = Lists.newArrayList();

    private final Handler defaultHandler = new Handler(null, new Container() {
        @Override public void handle(Request request, Response response) {
            try {
                monitor.logWarning(RequestDispatcher.class, "Received unexpected request for " + request);
                response.setCode(Status.NOT_FOUND.getCode());
                response.setText(Status.NOT_FOUND.getDescription());
                response.close();
            } catch (IOException e) {
                monitor.logWarning(RequestDispatcher.class, "Unable to respond with 404", e);
            }
        }
    });

    public RequestDispatcher(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void handle(Request request, Response response) {
        find(handlers, Handler.understanding(request), defaultHandler).handle(request, response);
    }

    public void register(Predicate<Request> condition, Container container) {
        handlers.add(new Handler(condition, container));
    }

    public static final class RequestPredicate implements Predicate<Request> {
        private final String method;

        private RequestPredicate(String method) {
            this.method = method;
        }

        @Override
        public boolean apply(Request request) {
            return this.method.equals(request.getMethod());
        }

        public Predicate<Request> forPath(String path) {
            return forPath(Predicates.equalTo(path));
        }

        public Predicate<Request> forPath(Predicate<String> path) {
            return Predicates.<Request>and(this, requestForPath(path));
        }
    }

    public static Predicate<Request> requestForPath(final Predicate<String> path) {
        return new Predicate<Request>() {
            @Override public boolean apply(Request request) {
                return path.apply(request.getPath().getPath());
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
