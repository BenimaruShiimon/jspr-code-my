package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class MainServlet extends HttpServlet {
  private static final String API_POSTS = "/api/posts";
  private static final String API_POSTS_ID_PATTERN = "/api/posts/\\d+";

  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = getPath(req);
      final var method = req.getMethod();

      if ("GET".equals(method) && API_POSTS.equals(path)) {
        controller.all(resp);
        return;
      }
      if ("GET".equals(method) && path.matches(API_POSTS_ID_PATTERN)) {
        controller.getById(extractId(path), resp);
        return;
      }
      if ("POST".equals(method) && API_POSTS.equals(path)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if ("DELETE".equals(method) && path.matches(API_POSTS_ID_PATTERN)) {
        controller.removeById(extractId(path), resp);
        return;
      }

      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (NotFoundException e) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (IOException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private String getPath(HttpServletRequest req) {
    return req.getRequestURI().replaceFirst("^" + req.getContextPath(), "");
  }

  private long extractId(String path) {
    return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
  }
}
