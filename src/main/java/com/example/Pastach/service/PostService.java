package com.example.Pastach.service;

import com.example.Pastach.model.Post;
import org.springframework.stereotype.Service;

import java.util.Optional;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PostService {
    private List<Post> posts = new ArrayList<>();

    public List<Post> findAll() {
        return posts;
    }

    public List<Post> searchPosts(String author) {
        return posts.stream()
                .filter(x -> Objects.equals(x.getAuthor(), author)).toList();
    }
    public Optional<Post> findById(int postId) {
        return posts.stream()
                .filter(x -> Objects.equals(x.getId(), postId))
                .findFirst();
    }


    public Post create(Post post) {
        posts.add(post);
        return post;
    }
}
