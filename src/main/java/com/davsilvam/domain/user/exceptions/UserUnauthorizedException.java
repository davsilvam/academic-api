package com.davsilvam.domain.user.exceptions;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException() {
        super("User not allowed to access this subject.");
    }

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
