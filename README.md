# SimpleServer: A Lightweight Java HTTP Server

## Overview

SimpleServer is a lightweight HTTP server implemented in Java using raw sockets. It provides routing, middleware support, and request/response handling similar to Express.js in Node.js. The server supports dynamic route parameters, middleware chaining, and custom error handling.

## Features

- **Custom HTTP Server**: Built using raw sockets (`ServerSocket` and `Socket`).
- **Routing System**: Supports static and dynamic routes (`/test/<id>`).
- **Middleware Support**: Implements a middleware system for request processing.
- **Custom Error Handling**: Allows defining a global error handler.
- **Threaded Client Handling**: Uses a thread pool to handle multiple client connections concurrently.

## Project Structure

```
com/simpleserver/
│── App.java            # Core server class
│── Main.java           # Entry point for running the server
│
├── client/
│   ├── Client.java     # Handles individual client connections
│
├── middleware/
│   ├── Middleware.java # Functional interface for middleware
│   ├── Next.java       # Functional interface for middleware chaining
│
├── request/
│   ├── Request.java    # Parses incoming HTTP requests
│   ├── router/
│   │   ├── Router.java # Routing logic (static and dynamic routes)
│
├── response/
│   ├── Response.java   # Builds and sends HTTP responses
│
├── utils/
│   ├── HttpStatus.java # Enum for HTTP status codes
```

## How It Works

### Server Initialization (`Main.java`)

- Creates an instance of `App`
- Registers global headers (`Content-Type: application/json`)
- Defines middleware for logging
- Defines routes (`GET /test/<id>`)
- Starts listening for requests

### Request Processing Flow

1. **Client connects** → `Client.java`
2. **Request is parsed** → `Request.java`
3. **Middlewares execute** → `Middleware.java`
4. **Route handler runs** → `Router.java`
5. **Response is sent** → `Response.java`

### Middleware Execution

Middleware functions are stored in a `Deque<Middleware>` and executed sequentially using:

```java
middleware.handle(req, res, () -> executeMiddlewares(req, res, middlewares));
```

### Dynamic Route Matching

- URLs like `/test/<id>/post/<postId>` are mapped to regex patterns.
- Extracted parameters are set in `Request.params`.

## Usage

### 1. Add Routes

```java
app.route("GET", "/hello", (req, res, next) -> {
    res.send(200, "Hello World!");
});
```

### 2. Add Middleware

```java
app.use((req, res, next) -> {
    System.out.println("Middleware executed");
    next.execute();
});
```

### 3. Start the Server

```java
app.listen(8080);
```

## Running Locally

To build and run the server locally, use the following Maven commands:

```sh
mvn clean compile
mvn exec:java
```

## Next Steps

- Add **HTTPS support**
- Implement **WebSocket support**
- Introduce **JSON serialization utilities**

## License

This project is open-source and available under the MIT License.
