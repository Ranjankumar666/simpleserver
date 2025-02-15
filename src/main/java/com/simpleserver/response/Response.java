package com.simpleserver.response;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private String version;
    private int status;
    private String message;
    private String body;
    private Socket client;
    private Map<String, String> headers = new HashMap<>();

    public Response(String version, Socket client) {
        this.version = version;
        this.client = client;
    }

    public String getVersion() {
        return this.version;
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString() {
        return this.version + " " + this.status + " " + this.message + "\r\n" + this.body;
    }

    public byte[] toBytes() {
        return this.toString().getBytes();
    }

    public void send() {
        try {
            client.getOutputStream().write(toBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String body) {
        setBody(body);
        try {
            client.getOutputStream().write(toBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(int status, String body) {
        setStatus(status);
        setBody(body);

        try {
            client.getOutputStream().write(toBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
