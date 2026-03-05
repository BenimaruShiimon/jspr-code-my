package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final List<Post> posts = new ArrayList<>();

    public List<Post> all() {
        List<Post> result = new ArrayList<>();
        for (Post post : posts) {
            if (Boolean.TRUE.equals(post.getRemoved())) {
                result.add(post);
            }
        }
        return result;
    }

    public Optional<Post> getById(long id) {
        for (Post post : all()) {
            if (post.getId() == id && !Boolean.TRUE.equals(post.getRemoved())) {
                return Optional.of(post);
            }
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            long newId = posts.stream()
                    .mapToLong(Post::getId)
                    .max()
                    .orElse(0) + 1;
            Post newPost = new Post(newId, post.getContent());
            posts.add(newPost);
            return newPost;
        } else {
            for (int i = 0; i < posts.size(); i++) {
              Post existingPost = posts.get(i);
                if (existingPost.getId() == post.getId()) {
                  if (Boolean.TRUE.equals(existingPost.getRemoved())){
                    throw new NotFoundException("Пост был удален!");
                  }
                    posts.set(i, post);
                    return post;
                }
            }
            posts.add(post);
            return post;
        }
    }

    public void removeById(long id) {
        for (Post post : posts) {
          if (post.getId() == id && Boolean.TRUE.equals(post.getRemoved())) {
            post.setRemoved(true);
            return;
          }
        }
    }
}
