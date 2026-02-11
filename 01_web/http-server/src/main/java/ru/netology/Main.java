package ru.netology;

import ru.netology.server.Server;

public class Main {
     static void main(String[] args) {
        Server server = new Server(9999);
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            responseStream.write("GET messages".getBytes());
            responseStream.flush();
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            responseStream.write("POST messages".getBytes());
            responseStream.flush();
        });
        server.listen();
    }
}