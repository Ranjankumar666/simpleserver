package com.simpleserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.simpleserver.client.Client;
import com.simpleserver.middleware.Middleware;
import com.simpleserver.request.router.Router;

import lombok.Getter;
import lombok.Setter;

/**
 * Hello world!
 */
@Getter
@Setter
public final class App {
    private volatile int port = 80;
    private volatile String host = "localhost";
    private final String version = "HTTP/1.0";
    private Map<String, String> headers = new ConcurrentHashMap<>();
    private ServerSocket socket = null;
    private Router router = new Router();
    private Executor threadManager = Executors.newCachedThreadPool();
    private Deque<Middleware> middlewares = new ConcurrentLinkedDeque<>();

    public App() {
    }

    public App(int port, String host) {
        this.setPort(port);
        this.setHost(host);
    }

    public App route(String method, String path, Middleware middleware) {
        router.register(method, path, middleware);
        return this;
    }

    public App use(Middleware middleware) {
        middlewares.push(middleware);
        return this;
    }

    public App all(String path, Middleware middleware) {
        String[] methods = new String[] { "GET", "PUT", "POST", "PATCH", "DELETE" };

        for (String method : methods) {
            router.register(method, path, middleware);
        }
        return this;
    }

    public App error(Middleware middleware) {
        router.setErrorHandler(middleware);
        return this;
    }

    public void listen() {
        this.run(this.getHost(), this.getPort());
    }

    public void listen(int port) {
        this.run(this.getHost(), port);
    }

    public void listen(String host, int port) {
        this.run(host, port);
    }

    public void run(String host, int port) {
        this.setHost(host);
        this.setPort(port);

        try {
            handleServerSocket();
            while (true) {
                try {
                    Socket client = socket.accept();
                    threadManager.execute(new Client(client, this));

                } catch (SocketException e) {
                    System.out.println("Client disconnected");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void handleServerSocket() throws IOException, SocketException {
        socket = new ServerSocket();
        socket.bind(new java.net.InetSocketAddress(this.getHost(), this.getPort()));

        System.out.println(
                "Server started at: " + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getLocalPort());

        final ServerSocket finalSocket = socket;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (finalSocket != null && !finalSocket.isClosed()) {
                    finalSocket.close();
                    System.out.println("Server socket closed.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }

}
