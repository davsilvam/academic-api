package com.davsilvam.exceptions.user;

public class MissingPropertiesException extends RuntimeException {
    public MissingPropertiesException() {
        super("Has missing properties!");
    }

    public MissingPropertiesException(String message) {
        super(message);
    }
}
