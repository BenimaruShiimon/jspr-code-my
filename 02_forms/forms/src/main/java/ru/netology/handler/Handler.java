package ru.netology.handler;

import ru.netology.service.Request;


import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface Handler {
    void handle(Request request, OutputStream outputStream) throws IOException;
}
