package com.davsilvam.domain.professor.exceptions;

public class ProfessorNotFoundException extends RuntimeException {
    public ProfessorNotFoundException() {
        super("Professor not found.");
    }

    public ProfessorNotFoundException(String message) {
        super(message);
    }
}
