package com.example.Pastach.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // apply to methods
@Retention(RetentionPolicy.RUNTIME)  // runtime check
public @interface RequireNonLockedUser {
    String message() default "Your account is locked. You cannot create or edit content.";
}