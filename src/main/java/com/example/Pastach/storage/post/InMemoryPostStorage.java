package com.example.Pastach.storage.post;

import com.example.Pastach.model.Post;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryPostStorage implements PostStorage {
    private final Map<Integer, Post> posts = new HashMap<>();
    int nextId = 1;

    @Override
    public Map<Integer, Post> findAll() {
        return posts;
    }

    @Override
    public Map<Integer, Post> searchPosts(String author, LocalDate creationDate) {
        return posts.values().stream()
                .filter(x -> x.getAuthor().equals(author) &&
                        !x.getCreationDate().toLocalDate().isBefore(creationDate)).
                collect(Collectors.toMap(Post::getId, post -> post));
    }

    @Override
    public Optional<Post> findById(int postId) {
        return Optional.ofNullable(posts.get(postId));
    }

    @Override
    public Post create(Post post) {
        post.setId(nextId);
        nextId++;
        posts.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> deleteById(int postId) {
        Post deletedPost = posts.get(postId);
        posts.remove(postId);
        return Optional.ofNullable(deletedPost);
    }

    @Override
    public Post updateById(Post post, int postId) {
        post.setId(postId);
        posts.put(postId, post);
        return post;
    }
}
