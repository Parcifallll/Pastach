package com.example.Pastach.controller;


import com.example.Pastach.model.User;
import com.example.Pastach.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
    @GetMapping("/users")
    public Map<String, User> findAll() {
        return userService.findAll();
    }

    @PutMapping("/users/{userId}") // update info
    public User update(@RequestBody User user, @PathVariable String userId) {
        return userService.updateById(user, userId);
    }

    @PostMapping("/user")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @DeleteMapping("/users/{userId}")
    public Optional<User> deleteById(@PathVariable String userId) {
        return userService.deleteById(userId);
    }
    */
    @GetMapping("/{login}")
    public Optional<User> getUSer(@PathVariable String login) {
        return userService.findUserById(login);
    }

}