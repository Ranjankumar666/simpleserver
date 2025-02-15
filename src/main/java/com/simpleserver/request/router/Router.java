package com.simpleserver.request.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.simpleserver.handler.Handler;
import com.simpleserver.request.Request;

// import com.simpleserver.handler.Handler;

public class Router {
    private Map<String, Boolean> statics = new ConcurrentHashMap<>();
    private Map<Pattern, String> dynamics = new ConcurrentHashMap<>();
    private Map<String, Map<String, Handler>> routes = new ConcurrentHashMap<>();

    public Map<String, Map<String, Handler>> getRoutes() {
        return routes;
    }

    public void add(String path) {
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

    public void register(String method, String path, Handler handler) {
        add(path);

        Map<String, Handler> routeRef = routes.getOrDefault(path, new ConcurrentHashMap<>());

        routeRef.put(method, handler);

        if (!routes.containsKey(path)) {
            routes.put(path, routeRef);
        }
    }

    public Handler setParams(Request req) {
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

    public ArrayList<String> extractIds(String target) {
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
