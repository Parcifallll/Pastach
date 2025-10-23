package com.example.Pastach.dao;

import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface PostDao {
    Collection<Post> findPostsByUser(String userId);

    Collection<Post> findAll();

    Collection<Post> searchPosts(String author, LocalDate creationDate);

    Post create(Post post);

    Optional<Post> deleteById(int postId);

    Post updateById(Post post, int postId);
}
