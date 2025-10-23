package com.example.Pastach.service;

import com.example.Pastach.dao.PostDao;
import com.example.Pastach.dao.UserDao;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;
import com.example.Pastach.storage.post.InMemoryPostStorage;
import com.example.Pastach.validation.PostValidation;
import com.example.Pastach.validation.UserValidation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class PostService {
    private final PostDao postDao;
    private final UserService userService;
    private final UserDao userDao;

    public PostService(PostDao postDao, UserService userService, UserDao userDao) {
        this.postDao = postDao;
        this.userService = userService;
        this.userDao = userDao;
    }

    public Collection<Post> findPostsByUser(String userId) {
        UserValidation.validateUserExists(userDao.findAll(), userId);
        return postDao.findPostsByUser(userId);
    }

    public Collection<Post> findAll() {
        return postDao.findAll();
    }

    public Collection<Post> searchPosts(String author, LocalDate creationDate) {
        return postDao.searchPosts(author, creationDate);
    }


    public Post create(Post post) {
        return postDao.create(post);
    }

    public Optional<Post> deleteById(int postId) {
        PostValidation.validatePostExists(postDao.findAll(), postId);
        return postDao.deleteById(postId);
    }

    public Post updateById(Post post, int postId) {
        PostValidation.validatePostExists(postDao.findAll(), postId);
        return postDao.updateById(post, postId);
    }
}
