package com.example.Pastach.dao;

import com.example.Pastach.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDao {
    Optional<User> findUserById(String id);

    List<User> findAll();
}
