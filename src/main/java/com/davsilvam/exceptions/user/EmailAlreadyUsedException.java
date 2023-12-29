package com.davsilvam.exceptions.user;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() {
        super("Email already used!");
    }

    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
