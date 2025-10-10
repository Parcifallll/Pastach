package com.example.Pastach.service;

import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Post;
import com.example.Pastach.validation.PostValidation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class PostService {
    private final Map<Integer, Post> posts = new HashMap<>();
    int nextId = 1;

    public Map<Integer, Post> findAll() {
        return posts;
    }

    public List<Post> searchPosts(String author, LocalDate creationDate) {
        return posts.values().stream()
                .filter(x -> x.getAuthor().equals(author) && !x.getCreationDate().toLocalDate().isBefore(creationDate)).toList();
    }

    public Optional<Post> findById(int postId) {
        PostValidation.validatePostExists(posts, postId);
        return Optional.ofNullable(posts.get(postId));
    }


    public Post create(Post post) {
        post.setId(nextId);
        nextId++;
        posts.put(post.getId(), post);
        return post;
    }

    public Optional<Post> deleteById(int postId) {
        PostValidation.validatePostExists(posts, postId);
        Post deletedPost = posts.get(postId);
        posts.remove(postId);
        return Optional.ofNullable(deletedPost);
    }

    public Post updateById(Post post, int postId) {
        PostValidation.validatePostExists(posts, postId);
        post.setId(postId);
        posts.put(postId, post);
        return post;
    }
}
