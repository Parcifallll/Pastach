package com.example.Pastach.exception;

public class InvalidEmailException extends RuntimeException { // create an unchecked exception
    public InvalidEmailException (String message) {
        super(message);
    }

}

