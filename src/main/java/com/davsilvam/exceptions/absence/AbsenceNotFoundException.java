package com.davsilvam.exceptions.absence;

public class AbsenceNotFoundException extends RuntimeException {
    public AbsenceNotFoundException() {
        super("Absence not found!");
    }

    public AbsenceNotFoundException(String message) {
        super(message);
    }
}
