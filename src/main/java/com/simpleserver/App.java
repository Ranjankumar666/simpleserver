package com.simpleserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.simpleserver.client.Client;
import com.simpleserver.handler.Handler;
import com.simpleserver.request.router.Router;

/**
 * Hello world!
 */
public final class App {
    private volatile int port = 80;
    private volatile String host = "localhost";
    private final String version = "HTTP/1.0";
    private Map<String, String> headers = new ConcurrentHashMap<>();
    private ServerSocket socket = null;
    private Router router = new Router();
    private Executor threadManager = Executors.newCachedThreadPool();

    public Router getRouter() {
        return router;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public String getVersion() {
        return version;
    }

    public ServerSocket getSocket() {
        return socket;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, Map<String, Handler>> getRoutes() {
        return router.getRoutes();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public App() {
    }

    public App(int port, String host) {
        this.setPort(port);
        this.setHost(host);
    }

    public App route(String method, String path, Handler handler) {
        router.register(method, path, handler);
        return this;
    }

    public App all(String path, Handler handler) {
        String[] methods = new String[] { "GET", "PUT", "POST", "PATCH", "DELETE" };

        for (String method : methods) {
            router.register(method, path, handler);
        }
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

    public void error(Handler handler) {
        Map<String, Handler> mp = new ConcurrentHashMap<>();
        mp.put("1", handler);
        this.router.getRoutes().put("ERROR", mp);
    }

}
