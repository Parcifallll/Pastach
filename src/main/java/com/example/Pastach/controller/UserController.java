package com.example.Pastach.controller;

import com.example.Pastach.exception.InvalidEmailException;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.model.User;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@RestController
public class UserController {
    private Set<User> users = new HashSet<>();

    @GetMapping("/users")
    public Set<User> findAll() {
        return users;
    }

    @PutMapping("/users") // update info
    public User update(@RequestBody User user) {
        if (user.getEmail() == null || Objects.equals(user.getEmail(), "") || !user.getEmail().contains("@")) {
            throw new UserAlreadyExistException("Invalid email address: " + user.getEmail());
        }
        users.add(user);
        return user; // in PUT and POST requests it is better to return the object/smth to confirm the success
    }

    @PostMapping("/user")
    public User create(@RequestBody User user) { // the annotation reports Spring to de-serialize a JSON to Java object (User)
        // User user = Gson.toJson(user, User.class); without an annotation
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new InvalidEmailException("Invalid email address: " + user.getEmail());
        }
        if (users.contains(user)) { // compare emails
            throw new UserAlreadyExistException("User " + user.getNickname() + " already exists");
        }
        users.add(user);
        return user;
    }
}