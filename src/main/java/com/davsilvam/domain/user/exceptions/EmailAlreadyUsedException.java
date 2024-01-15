package com.davsilvam.domain.user.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() {
        super("Email already used!");
    }

    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
