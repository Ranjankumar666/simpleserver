package com.simpleserver.request.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.simpleserver.middleware.Middleware;
import com.simpleserver.request.Request;

import lombok.Getter;
import lombok.Setter;

// import com.simpleserver.handler.Handler;

@Getter
@Setter
public class Router {
    private Map<String, Boolean> statics = new ConcurrentHashMap<>();
    private Map<Pattern, String> dynamics = new ConcurrentHashMap<>();
    private Map<String, Map<String, Deque<Middleware>>> routes = new ConcurrentHashMap<>();

    private Middleware errorHandler = null;

    private void add(String path) {
        if (path.contains("<")) {
            /**
             * String path = "/users/{id}/posts/{postId}";
             * String regexPath = path.replaceAll("<([^>]+)>", "([^/]+)");
             * System.out.println(regexPath);
             * /users/([^/]+)/posts/([^/]+)
             */

            String regexPath = path.replaceAll("<([^>]+)>", "([^/]+)");
            Pattern pattern = Pattern.compile("^" + regexPath + "$");

            dynamics.put(pattern, path);
        } else {
            statics.put(path, true);
        }

    }

    public void register(String method, String path, Middleware middleware) {
        add(path);

        var routeRef = routes.getOrDefault(path, new ConcurrentHashMap<>());

        var queue = routeRef.getOrDefault(method, new ConcurrentLinkedDeque<>());
        queue.addLast(middleware);

        if (!routeRef.containsKey(method)) {
            routeRef.put(method, queue);
        }

        if (!routes.containsKey(path)) {
            routes.put(path, routeRef);
        }
    }

    public Deque<Middleware> setParams(Request req) {
        String path = req.getPath();
        String method = req.getMethod();

        if (statics.containsKey(path)) {
            req.setParams(null);
            return routes.get(path).get(method);
        }

        for (Pattern p : dynamics.keySet()) {
            Matcher values = p.matcher(path);

            if (values.matches()) {

                ArrayList<String> paramIds = extractIds(dynamics.get(p));
                Map<String, String> params = new HashMap<>();

                if (paramIds.size() > 0) {
                    for (var i = 0; i < values.groupCount(); i++) {
                        params.put(paramIds.get(i), values.group(i + 1));
                    }
                }

                req.setParams(params);
                return routes.get(dynamics.get(p)).get(method);
            }
        }

        return null;
    }

    private ArrayList<String> extractIds(String target) {
        System.err.println(target);
        /* <([^/>]+)> this extracts every word more than 1 length with <> */
        Matcher keys = Pattern.compile("<([^/>]+)>").matcher(target);
        ArrayList<String> paramIds = new ArrayList<>();

        while (keys.find()) {
            paramIds.add(keys.group(1));
        }

        return paramIds;

    }
}
