package com.simpleserver.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.simpleserver.App;
import com.simpleserver.handler.Handler;
import com.simpleserver.request.Request;
import com.simpleserver.response.Response;

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
        Handler handler = server.getRouter().setParams(req);

        if (handler != null) {
            handler.handle(req, res);
        } else {
            handleError(req, res);
        }
        client.close();
    }

    public void handleError(Request req, Response res) throws IOException, SocketException {
        if (server.getRoutes().containsKey("ERROR")) {
            server.getRoutes().get("ERROR").get("1").handle(req, res);
        } else {
            client.getOutputStream().write("HTTP/1.0 404 Not Found\r\n\r\n".getBytes());
            client.getOutputStream().write("404 Not Found".getBytes());
        }
    }

}
