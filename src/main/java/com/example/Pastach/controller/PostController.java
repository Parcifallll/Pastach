package com.example.Pastach.controller;

import com.example.Pastach.model.Post;
import com.example.Pastach.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PostController { //manage Posts
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) { // DI
        this.postService = postService;
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
