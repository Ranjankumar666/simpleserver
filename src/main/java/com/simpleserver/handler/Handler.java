package com.simpleserver.handler;

import com.simpleserver.request.Request;
import com.simpleserver.response.Response;

@FunctionalInterface
public interface Handler {
    void handle(Request request, Response response);
}
