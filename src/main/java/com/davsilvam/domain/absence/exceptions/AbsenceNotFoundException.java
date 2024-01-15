package com.davsilvam.domain.absence.exceptions;

public class AbsenceNotFoundException extends RuntimeException {
    public AbsenceNotFoundException() {
        super("Absence not found!");
    }

    public AbsenceNotFoundException(String message) {
        super(message);
    }
}
