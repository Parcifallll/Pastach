package com.example.Pastach.service;

import com.example.Pastach.model.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

    public List<Post> searchPosts(String author, LocalDate creationDate) {
        return posts.stream()
                .filter(x -> x.getAuthor().equals(author) && !x.getCreationDate().toLocalDate().isBefore(creationDate)).toList();
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
