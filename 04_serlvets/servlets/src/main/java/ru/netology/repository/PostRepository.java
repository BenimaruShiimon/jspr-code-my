package ru.netology.repository;

import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Stub
public class PostRepository {
  private final List<Post> posts = new ArrayList<>();

  public List<Post> all() {
    if (posts.isEmpty()){
      return Collections.emptyList();
    } else {
      return new ArrayList<>(posts);
    }
  }

  public Optional<Post> getById(long id) {
    for (Post post : all()) {
      if (post.getId() == id)
        return Optional.of(post);
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
        if (posts.get(i).getId() == post.getId()) {
          posts.set(i, post);
          return post;
        }
      }
      posts.add(post);
      return post;
    }
  }

  public void removeById(long id) {
    posts.removeIf(post -> post.getId() == id);
  }
}
