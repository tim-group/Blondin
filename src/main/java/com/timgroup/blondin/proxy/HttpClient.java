package com.timgroup.blondin.proxy;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface HttpClient {

    void handle(String targetHost, int targetPort, Request request, Response response);

}
