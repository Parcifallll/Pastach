package com.example.Pastach.storage.post;

import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Post;

import java.time.LocalDate;
import java.util.*;

public interface PostStorage {
    Map<Integer, Post> findAll();

    Map<Integer, Post> searchPosts(String author, LocalDate creationDate);

    Optional<Post> findById(int postId);

    Post create(Post post);

    Optional<Post> deleteById(int postId);

    Post updateById(Post post, int postId);
}
