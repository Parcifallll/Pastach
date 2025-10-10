package com.example.Pastach.service;

import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Post;
import com.example.Pastach.storage.post.InMemoryPostStorage;
import com.example.Pastach.validation.PostValidation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final InMemoryPostStorage inMemoryPostStorage;

    public PostService(InMemoryPostStorage inMemoryPostStorage) {
        this.inMemoryPostStorage = inMemoryPostStorage;
    }

    public Map<Integer, Post> findAll() {
        return inMemoryPostStorage.findAll();
    }

    public Map<Integer, Post> searchPosts(String author, LocalDate creationDate) {
        return inMemoryPostStorage.searchPosts(author, creationDate);
    }

    public Optional<Post> findById(int postId) {
        PostValidation.validatePostExists(inMemoryPostStorage.findAll(), postId);
        return inMemoryPostStorage.findById(postId);
    }


    public Post create(Post post) {
        return inMemoryPostStorage.create(post);
    }

    public Optional<Post> deleteById(int postId) {
        PostValidation.validatePostExists(inMemoryPostStorage.findAll(), postId);
        return inMemoryPostStorage.deleteById(postId);
    }

    public Post updateById(Post post, int postId) {
        PostValidation.validatePostExists(inMemoryPostStorage.findAll(), postId);
        return inMemoryPostStorage.updateById(post, postId);
    }
}
