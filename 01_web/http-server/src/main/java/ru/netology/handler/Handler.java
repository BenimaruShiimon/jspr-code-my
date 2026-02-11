package ru.netology.handler;

import ru.netology.model.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Handler {
    void handle(Request request, BufferedOutputStream bufferedInputStream) throws IOException;
}
