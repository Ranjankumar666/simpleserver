package com.simpleserver;

public class Main {
    public static void main(String[] args) {
        App app = new App();

        app.error((req, res, next) -> {
            res.send(404, "Something went wrong!!!");
        });

        // app.all("/test/<id>/post/<postId>", (req, res, next) -> {
        // // res.setBody("Hello World!");

        // System.out.print(req.getBody());
        // System.out.print(req.getHeaders());
        // System.err.println(req.getParams());

        // res.setStatus(200);
        // res.send();
        // });

        // app.route("POST", "/test", (req, res, next) -> {
        // // res.setBody("Hello World!");
        // System.out.print(req.getJson());
        // System.out.print(req.getHeaders());

        // res.setStatus(200);
        // res.send();
        // });

        app.route("GET", "/test/<id>", (req, res, next) -> {
            System.out.println("I am running first");
            next.execute();
        });

        app.route("GET", "/test/<id>", (req, res, next) -> {
            // res.setBody("Hello World!");
            System.out.println(2);

            // res.setStatus(200);
            res.send(200, "Hello");
        });
        app.listen();
    }

}
