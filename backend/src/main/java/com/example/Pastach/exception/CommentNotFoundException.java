package com.example.Pastach.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {
        super("Comment with id " + commentId + " not found.");
    }
}
