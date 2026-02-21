package ru.netology.server;

import org.glassfish.grizzly.http.server.Response;
import ru.netology.handler.Handler;
import ru.netology.service.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Map<String, Handler> handlers = new HashMap<>(); // регистрация обработчиков
    private final Path publicDir = Path.of(".", "public"); // папки с HTML/CSS/JS файлами
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64); // ThreadPool на 64 потока
    private ServerSocket serverSocket;

    // список доступных путей к статическим файлам
    private final List<String> validPaths = List.of(
            "/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html",
            "/events.html", "/events.js");

    // регистрация обработчика
    public void addHandler(String method, String path, Handler handler) {
        handlers.put(method + " " + path, handler);
        System.out.println("Добавлен " + method + " " + path);
    }

    // запуск сервера
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port); // создает ServerSocket на указанном порту
            System.out.println("Сервер запущен на порту " + port);
            // бесконечный цикл ожидания подключений
            while (!threadPool.isShutdown()) {
                final Socket socket = serverSocket.accept();
                threadPool.execute(() -> serveClient(socket)); // ThreadPool
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // обработка подключения
    private void serveClient(Socket socket) {
        try (socket;
             final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            final var requestLine = in.readLine();
            if (requestLine == null) return;

            final var parts = requestLine.split(" ");
            if (parts.length != 3) return;

            // используем Request вместо String
            final var request = new Request(parts[0], parts[1]);

            // ищем Handler
            String handlerKey = request.getMethod() + " " + request.getPath();
            Handler handler = handlers.get(handlerKey);
            if (handler != null) {
                handler.handle(request, out); // вызываем handler
                return;
            }

            // при отсутствии handler → статический файл
            serveStatic(request.getPath(), out, request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // статические файлы
    private void serveStatic(String path, OutputStream out, Request request) throws IOException {
        if (!validPaths.contains(path)) {
            send404((BufferedOutputStream) out);
            return;
        }

        final var filePath = publicDir.resolve(path);
        if (!Files.exists(filePath)) {
            send404((BufferedOutputStream) out);
            return;
        }

        final var mimeType = Files.probeContentType(filePath);
        if (path.equals("/classic.html")) {
            sendClassicHtml((BufferedOutputStream) out, filePath, mimeType, request);
        } else {
            sendFile((BufferedOutputStream) out, filePath, mimeType);
        }
    }

    // установка текущего времени
    // добавлен Request
    private void sendClassicHtml(
            BufferedOutputStream out, Path filePath, String mimeType, Request request) throws IOException {
        final var template = Files.readString(filePath);

        // Query параметры в HTML
        String timeParam = request.getQueryParameter("time");
        String nameParam = request.getQueryParameter("name");

        String content = template.replace("{time}",
                timeParam != null ? timeParam : LocalDateTime.now().toString()).replace("{name}",
                nameParam != null ? nameParam : "Гость");

        final var contentBytes = content.getBytes(StandardCharsets.UTF_8);

        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType +
                "; charset=utf-8\r\n" + "Content-Length: " +
                contentBytes.length + "\r\n" +
                "Connection: close\r\n" + "\r\n")
                .getBytes(StandardCharsets.UTF_8));
        out.write(contentBytes);
        out.flush();
    }

    // файл не найден
    private void send404(BufferedOutputStream out) throws IOException {
        out.write(("HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());
        out.flush();
    }

    // отправка статических файлов HTML/CSS/JS/изображение
    private void sendFile(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
        final var length = Files.size(filePath);
        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" + "\r\n").getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    // остановка ThreadPool и ServerSocket
    public void stop() {
        threadPool.shutdown();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}