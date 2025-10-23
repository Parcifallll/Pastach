package com.example.Pastach.controller;

import com.example.Pastach.model.Post;
import com.example.Pastach.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PostController { //manage Posts
    private final PostService postService;

    public PostController(PostService postService) { // DI
        this.postService = postService;
    }

    @GetMapping("/posts/{userId}") // http://localhost:8080/posts/12
    public Collection<Post> getPosts(@PathVariable String userId) {
        return postService.findPostsByUser(userId);
    }

    @GetMapping("posts/search") // http://localhost:8080/posts/search?author=Roman
    public Collection<Post> searchPosts(@RequestParam String author, @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate creationDate) {
        return postService.searchPosts(author, creationDate);
    }

    @GetMapping("/posts") // endpoint
    public Collection<Post> findAll() {
        return postService.findAll();
    }

    @PostMapping(value = "/post") // endpoint
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @DeleteMapping("/posts/{postId}")
    public Optional<Post> deleteById(@PathVariable int postId) {
        return postService.deleteById(postId);
    }

    @PutMapping("/posts/{postId}")
    public Post updateById(@RequestBody Post post, @PathVariable int postId) {
        return postService.updateById(post, postId);
    }
}
