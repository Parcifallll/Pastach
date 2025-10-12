package com.example.Pastach.dao;

import com.example.Pastach.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findUserById(String id);
}
