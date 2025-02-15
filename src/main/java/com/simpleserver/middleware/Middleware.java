package com.simpleserver.middleware;

import com.simpleserver.request.Request;
import com.simpleserver.response.Response;

@FunctionalInterface
public interface Middleware {
    public void handle(Request request, Response response, Next next);
}
