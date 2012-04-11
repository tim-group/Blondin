package com.timgroup.blondin.proxy;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public interface ProxyClient {

    void handle(HttpRequest request, HttpResponse response);

}
