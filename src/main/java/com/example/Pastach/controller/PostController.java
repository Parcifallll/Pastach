package com.example.Pastach.controller;

import com.example.Pastach.model.Post;
import com.example.Pastach.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController { //manage Posts
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) { // DI
        this.postService = postService;
    }

    @GetMapping("posts/search") // http://localhost:8080/posts/search?author=Roman
    public List<Post> searchPosts(@RequestParam String author, @RequestParam @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate creationDate) {
        return postService.searchPosts(author, creationDate);
    }
    @GetMapping("/posts/{postId}") // http://localhost:8080/posts/12
    public Optional<Post> findById(@PathVariable int postId) {
        return postService.findById(postId);
    }
    @GetMapping("/posts") // endpoint
    public List<Post> findAll() {
        return postService.findAll();
    }


    @PostMapping(value = "/post") // endpoint
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }
}
