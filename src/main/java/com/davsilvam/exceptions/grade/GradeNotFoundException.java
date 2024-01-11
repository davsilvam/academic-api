package com.davsilvam.exceptions.grade;

public class GradeNotFoundException extends RuntimeException {
    public GradeNotFoundException() {
        super("Grade not found!");
    }

    public GradeNotFoundException(String message) {
        super(message);
    }
}
