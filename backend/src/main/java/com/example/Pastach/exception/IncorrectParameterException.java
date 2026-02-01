package com.example.Pastach.exception;

public class IncorrectParameterException extends RuntimeException{
    public IncorrectParameterException(String message) {
        super(message);
    }
}