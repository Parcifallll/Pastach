package com.example.Pastach.storage.post;

import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Post;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface PostStorage {
    List<Post> findAll();

    Optional<Post> findById(int postId);

    List<Post> searchPosts(String author, LocalDate creationDate);

    Post create(Post post);
}
