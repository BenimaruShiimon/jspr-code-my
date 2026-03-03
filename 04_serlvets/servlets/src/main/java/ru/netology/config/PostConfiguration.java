package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

@Configuration
public class PostConfiguration {
    @Bean
    public PostRepository postRepository() {
        return new PostRepository();
    }

    @Bean
    public PostService postService() {
        PostService service = new PostService(postRepository());
        return service;
    }

    @Bean
    public PostController postController() {
       PostController controller = new PostController(postService());
       return controller;
    }
}
