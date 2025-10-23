package com.example.Pastach.validation;

import com.example.Pastach.exception.InvalidEmailException;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.User;

import java.util.List;
import java.util.Map;

public class UserValidation {
    public static void validateEmail(String email) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            throw new InvalidEmailException("Invalid email address: " + email);
        }
    }

    public static void validateUserAlreadyExists(Map<String, User> users, User user, String field) {
        if (field.equals("email")) {
            boolean emailExists = users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()));
            if (emailExists) {
                throw new UserAlreadyExistException("User with email " + user.getEmail() + " already exists");
            }
        } else if (field.equals("id")) {
            boolean idExists = users.values().stream().anyMatch(u -> u.getId().equals(user.getId()));
            if (idExists) {
                throw new UserAlreadyExistException("User with id " + user.getId() + " already exists");
            }
        }
    }


    public static void validateUserExists(List<User> users, String userId) {
        if (!users.contains(userId)) {
            throw new UserNotFoundException("User with id " + userId + " is not found");
        }
    }

}
