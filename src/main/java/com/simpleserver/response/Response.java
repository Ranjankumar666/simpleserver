package com.simpleserver.response;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.simpleserver.utils.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setStatus(int status) {
        this.status = status;
        setMessage(HttpStatus.fromCodeToString(status));
    }

    public String toString() {
        return this.version + " " + this.status + " " + this.message + "\r\n" + this.getHeaders() + "\r\n\r\n" +
                this.body;
    }

    public byte[] toBytes() {
        return buildResponse().getBytes();
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

    public String buildResponse() {
        StringBuilder response = new StringBuilder();
        String statusLine = this.version + " " + this.status + " " + this.message;
        response.append(statusLine).append("\r\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        if (!headers.containsKey("Content-Length")) {
            response.append("Content-Length").append(":").append(body.getBytes().length).append("\r\n");
        }

        response.append("\r\n");
        response.append(body);

        return response.toString();
    }

}
