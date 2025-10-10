package com.example.Pastach.validation;

import com.example.Pastach.exception.InvalidEmailException;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.User;

import java.util.Map;

public class UserValidation {
    public static void validateEmail(String email) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            throw new InvalidEmailException("Invalid email address: " + email);
        }
    }

    public static void validateUserAlreadyExists(Map<Integer, User> users, User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException("User with email " + user.getEmail() + " already exists");
        }
    }

    public static void validateUserExists(Map<Integer, User> users, int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User with id " + userId + " is not found");
        }
    }

}
