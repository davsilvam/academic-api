package com.davsilvam.exceptions.subject;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException() {
        super("Subject not found.");
    }

    public SubjectNotFoundException(String message) {
        super(message);
    }
}
