package com.example.Pastach.service;

import com.example.Pastach.exception.InvalidEmailException;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.User;
import com.example.Pastach.validation.UserValidation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private Map<Integer, User> users = new HashMap<>();
    int nextId = 1;

    public Map<Integer, User> findAll() {
        return users;
    }

    public User updateById(User user, int userId) {
        UserValidation.validateUserExists(users, userId);
        if (!user.getEmail().equals(users.get(userId).getEmail())) {
            UserValidation.validateEmail(user.getEmail());
            UserValidation.validateUserAlreadyExists(users, user);
        }
        user.setId(userId);
        users.put(userId, user);
        return user; // in PUT and POST requests it is better to return the object/smth to confirm the success
    }

    public User create(User user) {
        UserValidation.validateEmail(user.getEmail());
        UserValidation.validateUserAlreadyExists(users, user);
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> deleteById(int userId) {
        UserValidation.validateUserExists(users, userId);
        User deletedUser = users.get(userId);
        users.remove(userId);
        return Optional.ofNullable(deletedUser);
    }

    public Optional<User> findById(int userId) {
        UserValidation.validateUserExists(users, userId);
        return Optional.ofNullable(users.get(userId));
    }
}
