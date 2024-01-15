package com.davsilvam.domain.absence.dtos;

import java.util.Date;
import java.util.Optional;

public record UpdateAbsenceRequest(Optional<String> date, Optional<Integer> amount) {
}
