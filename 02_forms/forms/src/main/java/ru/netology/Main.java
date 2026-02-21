package ru.netology;

import ru.netology.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        server.addHandler("GET", "/api/hello",
                (request, responseStream) -> {
                    responseStream.write(("HTTP/1.1 200 OK\r\nContent-Length: 13\r\n\r\n" +
                            "Привет, API!").getBytes());
                    responseStream.flush();
                });

        server.start(9999);
    }
}
