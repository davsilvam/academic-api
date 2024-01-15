package com.davsilvam.domain.absence.exceptions;

public class InvalidAbsenceDateException extends RuntimeException {
    public InvalidAbsenceDateException() {
        super("Invalid absence date.");
    }

    public InvalidAbsenceDateException(String message) {
        super(message);
    }
}
