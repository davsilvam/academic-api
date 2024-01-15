package com.davsilvam.domain.absence.dtos;

import java.util.Date;
import java.util.UUID;

public record CreateAbsenceRequest(String date, Integer amount, UUID subject_id) {
}
