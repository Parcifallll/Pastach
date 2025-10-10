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

    public Map<Integer, User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    public User updateById(User user, int userId) {
        UserValidation.validateUserExists(inMemoryUserStorage.findAll(), userId);
        if (!user.getEmail().equals(inMemoryUserStorage.findAll().get(userId).getEmail())) {
            UserValidation.validateEmail(user.getEmail());
            UserValidation.validateUserAlreadyExists(inMemoryUserStorage.findAll(), user);
        }
        return inMemoryUserStorage.updateById(user, userId); // in PUT and POST requests it is better to return the object/smth to confirm the success
    }

    public User create(User user) {
        UserValidation.validateEmail(user.getEmail());
        UserValidation.validateUserAlreadyExists(findAll(), user);
        return inMemoryUserStorage.create(user);

    }

    public Optional<User> deleteById(int userId) {
        UserValidation.validateUserExists(inMemoryUserStorage.findAll(), userId);
        return inMemoryUserStorage.deleteById(userId);
    }

    public Optional<User> findById(int userId) {
        UserValidation.validateUserExists(inMemoryUserStorage.findAll(), userId);
        return inMemoryUserStorage.findById(userId);
    }
}
