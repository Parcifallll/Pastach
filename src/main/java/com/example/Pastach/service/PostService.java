package com.example.Pastach.service;

import com.example.Pastach.dao.PostDao;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;
import com.example.Pastach.storage.post.InMemoryPostStorage;
import com.example.Pastach.validation.PostValidation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class PostService {
    private final PostDao postDao;
    private final UserService userService;

    public PostService(PostDao postDao, UserService userService) {
        this.postDao = postDao;
        this.userService = userService;
    }

    public Collection<Post> findPostsByUser(String userId) {
        User user = userService.findUserById(userId);
        return postDao.findPostsByUser(user);
    }

//    public Map<Integer, Post> findAll() {
//        return inMemoryPostStorage.findAll();
//    }
//
//    public Map<Integer, Post> searchPosts(String author, LocalDate creationDate) {
//        return inMemoryPostStorage.searchPosts(author, creationDate);
//    }
//
//
//
//    public Post create(Post post) {
//        return inMemoryPostStorage.create(post);
//    }
//
//    public Optional<Post> deleteById(int postId) {
//        PostValidation.validatePostExists(inMemoryPostStorage.findAll(), postId);
//        return inMemoryPostStorage.deleteById(postId);
//    }
//
//    public Post updateById(Post post, int postId) {
//        PostValidation.validatePostExists(inMemoryPostStorage.findAll(), postId);
//        return inMemoryPostStorage.updateById(post, postId);
//    }
}
