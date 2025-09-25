package com.example.Pastach.controller;


import com.example.Pastach.model.User;
import com.example.Pastach.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Set<User> findAll() {
        return userService.findAll();
    }

    @PutMapping("/users") // update info
    public User update(@RequestBody User user)
    {
        return userService.update(user);
    }

    @PostMapping("/user")
    public User create(@Valid @RequestBody User user){
        return userService.create(user);
    }

    @GetMapping("users/{userId}")
    public Optional<User> findById(@PathVariable int userId) { return userService.findById(userId); }

}