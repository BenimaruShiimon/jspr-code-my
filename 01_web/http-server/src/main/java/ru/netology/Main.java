package ru.netology;

import ru.netology.server.Server;

import java.nio.charset.StandardCharsets;

public class Main {
     static void main(String[] args) {
        Server server = new Server(4);

        server.addHandler(
                "GET",
                "/messages",
                (request, response) -> {
                    response.send("HTTP/1.1 200 OK");
                }
        );

        server.listener(8083);
     }
}