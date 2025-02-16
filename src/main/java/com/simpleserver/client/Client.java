package com.simpleserver.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import com.simpleserver.App;
import com.simpleserver.middleware.Middleware;
import com.simpleserver.request.Request;
import com.simpleserver.response.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client implements Runnable {
    private Socket client;
    private App server;

    public Client(Socket socket, App server) {
        client = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle() throws IOException, SocketException {

        Request req = new Request(client);
        Response res = new Response(server.getVersion(), client);
        var reqHeaders = req.getHeaders();

        if (!reqHeaders.containsKey("Host") && !reqHeaders.containsKey("host")) {
            res.send(400, "Bad Request: Missing Host Header");
            client.close();
            return;
        }

        // default headers
        for (Map.Entry<String, String> entry : server.getHeaders().entrySet()) {
            res.setHeader(entry.getKey(), entry.getValue());
        }

        handleMiddlewareExecution(req, res);
        String connection = reqHeaders.get("Connection");

        if (connection == null) {
            connection = reqHeaders.get("connection");
        }
        if (connection != null && connection.equalsIgnoreCase("close")) {
            client.close();
            return;
        }

    }

    private void handleMiddlewareExecution(Request req, Response res) throws IOException {
        Deque<Middleware> serverMiddlewares = server.getMiddlewares();
        Deque<Middleware> handlers = server.getRouter().getHandlers(req);
        Deque<Middleware> middlewares = new ArrayDeque<>();

        if (handlers == null)
            handlers = new ArrayDeque<>();
        if (serverMiddlewares == null)
            serverMiddlewares = new ArrayDeque<>();

        middlewares.addAll(serverMiddlewares);
        middlewares.addAll(handlers);

        if (!middlewares.isEmpty()) {
            executeMiddlewares(req, res, middlewares);
        } else {
            handleError(req, res);
        }

    }

    private void executeMiddlewares(Request req, Response res, Deque<Middleware> handlers) {
        if (handlers.isEmpty())
            return;

        Middleware middleware = handlers.poll();
        middleware.handle(req, res, () -> {
            executeMiddlewares(req, res, handlers);
        });
    }

    public void handleError(Request req, Response res) throws IOException, SocketException {

        if (server.getRouter().getErrorHandler() != null) {
            server.getRouter().getErrorHandler().handle(req, res, () -> {
            });
        } else {
            client.getOutputStream().write("HTTP/1.0 404 Not Found\r\n\r\n".getBytes());
            client.getOutputStream().write("404 Not Found".getBytes());
        }

    }

}
