package com.example.Pastach.storage.user;

import com.example.Pastach.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    Map<String, User> findAll();
    User updateById(User user, String userId);
    User create(User user);
    Optional<User> findById(String userId);
    Optional<User> deleteById(String userId);
}
