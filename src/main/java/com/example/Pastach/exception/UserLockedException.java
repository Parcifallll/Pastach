package com.example.Pastach.exception;

public class UserLockedException extends RuntimeException {
    public UserLockedException(String message) {
        super(message);
    }
    
    public UserLockedException() {
        super("Your account is locked. You cannot create or edit posts.");
    }
}