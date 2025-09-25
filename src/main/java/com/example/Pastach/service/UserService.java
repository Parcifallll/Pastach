package com.example.Pastach.service;

import com.example.Pastach.exception.InvalidEmailException;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private Set<User> users = new HashSet<>();

    public Set<User> findAll() {
        return users;
    }

    public User update(User user) {
        if (user.getEmail() == null || Objects.equals(user.getEmail(), "") || !user.getEmail().contains("@")) {
            throw new InvalidEmailException("Invalid email address: " + user.getEmail());
        }
        users.add(user);
        return user; // in PUT and POST requests it is better to return the object/smth to confirm the success
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new InvalidEmailException("Invalid email address: " + user.getEmail());
        }
        if (users.contains(user)) { // compare emails
            throw new UserAlreadyExistException("User " + user.getNickname() + " already exists");
        }
        users.add(user);
        return user;
    }

    public Optional<User> findById(int userId) {
        return Optional.ofNullable(users.stream()
                .filter(x -> x.getId() == (userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " is not found")));
    }
}
