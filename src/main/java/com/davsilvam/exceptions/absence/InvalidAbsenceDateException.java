package com.davsilvam.exceptions.absence;

public class InvalidAbsenceDateException extends RuntimeException {
    public InvalidAbsenceDateException() {
        super("Invalid absence date.");
    }

    public InvalidAbsenceDateException(String message) {
        super(message);
    }
}
