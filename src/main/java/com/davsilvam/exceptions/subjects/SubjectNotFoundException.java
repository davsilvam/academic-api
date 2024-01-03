package com.davsilvam.exceptions.subjects;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException() {
        super("Subject not found.");
    }

    public SubjectNotFoundException(String message) {
        super(message);
    }
}
