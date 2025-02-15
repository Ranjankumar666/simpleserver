# SimpleServer

A lightweight HTTP server built in Java 11, inspired by Express.js. It supports:

- **Routing** with dynamic path parameters
- **Multithreading** for handling multiple requests concurrently
- **Basic request parsing** for query params, headers, and request body

## 🚀 Features

- **Lightweight & Fast**: Minimal dependencies, runs efficiently.
- **Dynamic Routing**: Supports routes with parameters (`/users/{id}`).
- **Multithreading**: Handles multiple client connections concurrently.
- **Basic HTTP Methods**: Supports GET, POST, PUT, DELETE, etc.


## 📦 Installation

### Prerequisites

- **Java 11+** installed
- **Apache Maven** installed

### Clone the Repository

```sh
git clone https://github.com/Ranjankumar666/simpleserver.git
cd simpleserver
```

### Build the Project

```sh
mvn clean mvn clean compile
```

## 🚀 Usage

### Run the Server

```sh
mvn exec:java
```

### Define Routes

You can define routes inside the `App.java` file:

```java
app.route("POST", "/hello", (req, res) -> {
    res.send("Hello, World!");
});
```

### Dynamic Routes with Parameters

```java
app.route("POST","/users/{id}", (req, res) -> {
    String userId = req.getParam("id");
    res.send("User ID: " + userId);
});
```

### Handling POST Requests

```java
server.route("POST", "/submit", (req, res) -> {
    String body = req.getBody();
    res.send("Received: " + body);
});
```



## 🏗 Architecture

```plaintext
- App.java      -> Entry point, sets up the server and routes and Server implementating and threadinh
- Request.java  -> Parses HTTP requests.
- Response.java -> Handles HTTP responses.
- Router.java   -> Manages route handling.
- Client.java  -> Spawns a thread for each client request

```

## 🛠 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m 'Add feature'`)
4. Push to the branch (`git push origin feature-name`)
5. Open a Pull Request

## 📜 In Progress

1. Add static file sharing
2. Multipart form support
3. Middleware support
