package com.example.Pastach.storage.user;

import com.example.Pastach.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    Map<Integer, User> findAll();
    User updateById(User user, int userId);
    User create(User user);
    Optional<User> findById(int userId);
    Optional<User> deleteById(int userId);
}
