package com.davsilvam.exceptions.subjects;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException() {
        super("User not allowed to access this subject.");
    }

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
