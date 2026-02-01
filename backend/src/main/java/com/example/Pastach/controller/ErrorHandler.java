package com.example.Pastach.controller;

import com.example.Pastach.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.Pastach")
public class ErrorHandler {
    public record ErrorResponse(String type, String message) {
    }

    public record ValidationError(String field, String message) {
    }

    public record ValidationResponse(String type, List<ValidationError> details) {
    }

    // common for @Valid (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResponse handleValidation(MethodArgumentNotValidException e) {
        List<ValidationError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new ValidationError(err.getField(), err.getDefaultMessage()))
                .toList();
        return new ValidationResponse("Validation failed", errors);
    }

    // common for NotFound (404)
    @ExceptionHandler({PostNotFoundException.class, UserNotFoundException.class, CommentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(RuntimeException e) {
        return new ErrorResponse("Not found", e.getMessage());
    }

    // Common for BadRequest (400)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException e) {
        return new ErrorResponse("Bad request", e.getMessage());
    }

    // UserAlreadyExist (409)
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExist(UserAlreadyExistException e) {
        return new ErrorResponse("Conflict", e.getMessage());
    }

    // Authentication (401)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthentication(AuthenticationException e) {
        return new ErrorResponse("Unauthorized", e.getMessage());
    }

    // AccessDenied (403)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException e) {
        return new ErrorResponse("Forbidden", e.getMessage());
    }

    @ExceptionHandler(UserLockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUserLocked(UserLockedException e) {
        return new ErrorResponse("Forbidden", e.getMessage());
    }

    // common fallback (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception e) {
        e.printStackTrace();
        return new ErrorResponse("Internal error", "Something went wrong");
    }
}