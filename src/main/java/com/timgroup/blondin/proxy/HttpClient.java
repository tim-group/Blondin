package com.timgroup.blondin.proxy;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface HttpClient {

    void handle(String host, int port, Request request, Response response);

}
