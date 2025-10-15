package com.example.Pastach.dao;

import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;

import java.util.Collection;

public interface PostDao {
    Collection<Post> findPostsByUser(User user);
}
