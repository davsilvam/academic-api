package com.davsilvam.dtos.absence;

import java.util.Date;
import java.util.Optional;

public record UpdateAbsenceRequest(Optional<String> date, Optional<Integer> amount) {
}
