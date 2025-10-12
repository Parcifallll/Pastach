package com.example.Pastach.service;

import com.example.Pastach.model.User;
import com.example.Pastach.storage.user.InMemoryUserStorage;
import com.example.Pastach.validation.UserValidation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Map<String, User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    public User updateById(User user, String userId) {
        UserValidation.validateUserExists(inMemoryUserStorage.findAll(), userId);
        User existingUser = inMemoryUserStorage.findAll().get(userId);
        boolean emailChanged = !user.getEmail().equals(existingUser.getEmail());
        boolean idChanged = !user.getId().equals(existingUser.getId());

        if (emailChanged) {
            UserValidation.validateEmail(user.getEmail());
            UserValidation.validateUserAlreadyExists(inMemoryUserStorage.findAll(), user, "email");
        }

        if (idChanged) {
            UserValidation.validateUserAlreadyExists(inMemoryUserStorage.findAll(), user, "id");
        }

        return inMemoryUserStorage.updateById(user, userId);
    }


    public User create(User user) {
        UserValidation.validateUserAlreadyExists(inMemoryUserStorage.findAll(), user, "email");
        UserValidation.validateEmail(user.getEmail());
        UserValidation.validateUserAlreadyExists(inMemoryUserStorage.findAll(), user, "id");
        return inMemoryUserStorage.create(user);
    }


    public Optional<User> deleteById(String userId) {
        UserValidation.validateUserExists(inMemoryUserStorage.findAll(), userId);
        return inMemoryUserStorage.deleteById(userId);
    }

    public Optional<User> findById(String userId) {
        UserValidation.validateUserExists(inMemoryUserStorage.findAll(), userId);
        return inMemoryUserStorage.findById(userId);
    }
}
