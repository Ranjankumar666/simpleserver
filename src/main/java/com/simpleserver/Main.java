package com.simpleserver;

public class Main {
    public static void main(String[] args) {
        App app = new App();

        app.all("/test/<id>/post/<postId>", (req, res) -> {
            // res.setBody("Hello World!");

            System.out.print(req.getBody());
            System.out.print(req.getHeaders());
            System.err.println(req.getParams());

            res.setStatus(200);
            res.send();
        });

        app.route("POST", "/test", (req, res) -> {
            // res.setBody("Hello World!");
            System.out.print(req.getJson());
            System.out.print(req.getHeaders());

            res.setStatus(200);
            res.send();
        });

        app.route("GET", "/test/<id>", (req, res) -> {
            // res.setBody("Hello World!");
            System.out.print(req.getJson());
            System.out.print(req.getHeaders());
            System.err.println(req.getParams());

            res.setStatus(200);
            res.send();
        });
        app.listen();
    }

}
