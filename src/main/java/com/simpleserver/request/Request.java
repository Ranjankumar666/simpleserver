package com.simpleserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class Request {
    private String method;
    private String path;
    private String version;
    private Socket client;
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();
    private HashMap<String, String> form = new HashMap<>();
    private JSONObject json;
    private Map<String, String> queryString = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public Request(Socket client) throws IOException {
        this.client = client;
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        String line = reader.readLine();

        String[] parts = line.split(" ");
        setMethod(parts[0].trim());
        setPath(parts[1].trim());
        setVersion(parts[2].trim());

        if (this.path.contains("?")) {
            String[] parts2 = this.path.split("\\?");
            this.path = parts2[0];
            String[] parts3 = parts2[1].split("&");

            for (String part : parts3) {
                String[] parts4 = part.split("=");
                this.queryString.put(parts4[0], parts4[1]);
            }
        }

        // Headers
        line = reader.readLine();
        int contentLength = 0;

        while (line != null && !line.isEmpty()) {
            String[] hParts = line.split(":");
            this.headers.put(hParts[0].trim(), hParts[1].trim());
            line = reader.readLine();

            if (hParts[0].trim().equalsIgnoreCase("Content-Length")) {
                contentLength = Integer.parseInt(hParts[1].trim());
            }

        }

        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            int readChars = reader.read(bodyChars, 0, contentLength);
            if (readChars > 0) {
                body.append(bodyChars, 0, readChars);
            }
            parseBody();
        }

        // reader.close();
    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getVersion() {
        return this.version;
    }

    public Socket getClient() {
        return this.client;
    }

    public StringBuilder getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public JSONObject getJson() {
        return json;
    }

    public HashMap<String, String> getForm() {
        return form;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void parseBody() {
        String contentType = headers.getOrDefault("Content-Type", "");

        switch (contentType.toLowerCase()) {
            case "application/json":
                json = new JSONObject(body.toString());
                break;
            case "application/x-www-form-urlencoded":
                String[] pairs = body.toString().split("&");

                for (String pair : pairs) {
                    String[] pairParts = pair.split("=");
                    if (pairParts.length == 2)
                        try {
                            form.put(URLDecoder.decode(pairParts[0], StandardCharsets.UTF_8.name()),
                                    URLDecoder.decode(pairParts[1],
                                            StandardCharsets.UTF_8.name()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                }
                break;
            case "multipart/form-data":
                break;
            case "text/plain":
                break;
            default:
                break;
        }
    }
}
