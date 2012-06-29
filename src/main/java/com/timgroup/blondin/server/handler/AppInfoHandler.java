package com.timgroup.blondin.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import com.google.common.base.Supplier;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.status.BlondinStatus;
import com.timgroup.tucker.info.ApplicationInformationHandler;
import com.timgroup.tucker.info.servlet.WebResponse;

public final class AppInfoHandler implements Container {

    private static final String INFO_PATH = "/info";
    
    private final Monitor monitor;
    private final BlondinStatus status;
    private final ApplicationInformationHandler handler;
    
    public AppInfoHandler(Monitor monitor,
                          Supplier<Iterable<String>> expensiveResourcesListSupplier)
    {
        this.monitor = monitor;
        this.status = new BlondinStatus(expensiveResourcesListSupplier);
        this.handler = new ApplicationInformationHandler(this.status.generator());
    }

    @Override
    public void handle(Request request, Response response) {
        try {
            final String path = request.getPath().getPath();
            handler.handle(path.substring(INFO_PATH.length()), new ResponseWrapper(INFO_PATH, response));
        } catch (IOException e) {
            monitor.logError(AppInfoHandler.class, "Failed to respond to status page request", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                monitor.logError(AppInfoHandler.class, "Failed to close status page request", e);
            }
        }
    }

    private static final class ResponseWrapper implements WebResponse {
        private final String path;
        private final Response response;

        public ResponseWrapper(String path, Response response) {
            this.path = path;
            this.response = response;
        }

        @Override
        public OutputStream respond(String contentType, String characterEncoding) throws IOException {
            response.set("Content-Type", contentType);
            response.add("Content-Type", "charset=" + characterEncoding);
            
            //response.setContentLength(length)
            return response.getOutputStream();
        }

        @Override
        public void reject(int statusCode, String message) throws IOException {
            response.setCode(statusCode);
            response.setText(Status.getDescription(statusCode));
            response.getOutputStream().write(message.getBytes(Charset.forName("utf-8")));
        }

        @Override
        public void redirect(String relativePath) throws IOException {
            response.setCode(Status.MOVED_PERMANENTLY.getCode());
            response.setText(Status.MOVED_PERMANENTLY.getDescription());
            response.set("Location", path + relativePath);
        }
    }
}
