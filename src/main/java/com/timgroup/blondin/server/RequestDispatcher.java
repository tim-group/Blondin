package com.timgroup.blondin.server;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

public final class RequestDispatcher implements Container {

    @Override
    public void handle(Request request, Response response) {
        writeNotFound(response);
    }

    private void writeNotFound(Response response) {
        try {
            response.setCode(HttpURLConnection.HTTP_NOT_FOUND);
            response.setText("Not Found");
            response.close();
        } catch (IOException e) {
            
        }
    }
}
