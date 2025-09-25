package com.example.Pastach.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException (String message) {
        super(message);
    }
}