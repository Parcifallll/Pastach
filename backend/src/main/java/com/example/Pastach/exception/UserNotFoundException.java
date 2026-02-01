package com.example.Pastach.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " not found");
    }
}
