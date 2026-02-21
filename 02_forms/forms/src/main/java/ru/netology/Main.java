package ru.netology;

import ru.netology.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.addHandler(
                "GET",
                "/api/greeting",
                (request, response) -> {
                    response.write(("HTTP/1.1 200 OK\r\nContent-Length: 13\r\n\r\n" +
                            "Привет, API!").getBytes());
                response.flush();
                });

        server.start(9999);
    }
}
