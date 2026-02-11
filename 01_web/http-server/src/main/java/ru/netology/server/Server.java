package ru.netology.server;

import ru.netology.handler.Handler;
import ru.netology.model.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService executorService;
    private final Map<String, Map<String, Handler>> handlerRegistry;

    public Server(int port) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(64);
        this.handlerRegistry = new HashMap<>();
    }

    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порте " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream())
        ) {
            String requestLine = reader.readLine();
            String[] parts = requestLine.split(" ");

            if (parts.length != 3) {
                return;
            }

            String method = parts[0];
            String path = parts[1];

            Request request = parseRequest(reader, method, path);
            Handler handler = findHandler(method, path);

            if (handler != null) {
                handler.handle((com.sun.net.httpserver.Request) request, writer);
            } else {
                sendError(writer, 404, "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Request parseRequest(BufferedReader reader, String method, String path) throws IOException {
        return new Request(method, path, /* headers */ null, /* body */ null);
    }

    private Handler findHandler(String method, String path) {
        Map<String, Handler> methodHandlers = handlerRegistry.getOrDefault(method, new HashMap<>());
        return methodHandlers.get(path);
    }

    public void addHandler(String method, String path, Handler handler) {
        handlerRegistry.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }

    private void sendError(BufferedOutputStream writer, int statusCode, String message) throws IOException {
        String errorResponse = String.format(
                "HTTP/1.1 %d %s\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n",
                statusCode,
                message
        );
        writer.write(errorResponse.getBytes());
        writer.flush();
    }
}