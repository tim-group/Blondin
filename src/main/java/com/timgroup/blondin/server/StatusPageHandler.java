package com.timgroup.blondin.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import com.google.common.base.Supplier;
import com.google.common.io.ByteStreams;
import com.timgroup.blondin.diagnostics.Monitor;
import com.timgroup.blondin.server.status.BlondinStatus;
import com.timgroup.tucker.info.ApplicationInformationHandler;
import com.timgroup.tucker.info.servlet.WebResponse;
import com.timgroup.tucker.info.status.StatusPage;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public final class StatusPageHandler implements Container {

    private final Monitor monitor;
    private final BlondinStatus status;
    private final Supplier<BlondinServerStatus> serverStatusSupplier;
    private final ApplicationInformationHandler handler;
    
    public StatusPageHandler(Monitor monitor,
                             Supplier<BlondinServerStatus> serverStatusSupplier,
                             Supplier<Iterable<String>> expensiveResourcesListSupplier)
    {
        this.monitor = monitor;
        this.serverStatusSupplier = serverStatusSupplier;
        this.status = new BlondinStatus(expensiveResourcesListSupplier);
        this.handler = new ApplicationInformationHandler(this.status.generator());
    }

    @Override
    public void handle(Request request, Response response) {
        try {
            final String path = request.getPath().getPath();
            if (path.equals("/info/version")) {
                handler.handle(path.substring(5), new ResponseWrapper("/info", response));
                return;
            }
            
            if (path.equals("/status-page.css")) {
                writeStatusPageCssTo(response);
                return;
            }
            
            if (BlondinServerStatus.SUSPENDED.equals(serverStatusSupplier.get())) {
                response.setCode(HTTP_UNAVAILABLE);
                response.setText("Service Unavailable");
            }
            response.set("Content-Type", "text/xml");
            status.writeTo(response.getOutputStream());
            response.close();
        } catch (IOException e) {
            monitor.logError(StatusPageHandler.class, "Failed to respond to status page request", e);
        }
    }

    private void writeStatusPageCssTo(Response response) {
        try {
            response.set("Content-Type", "text/css");
            ByteStreams.copy(StatusPage.class.getResourceAsStream("status-page.css"), response.getOutputStream());
            response.close();
        } catch (IOException e) {
            monitor.logError(StatusPageHandler.class, "Failed to respond to status page css request", e);
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
        public void reject(int status, String message) throws IOException {
            response.setCode(status);
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
