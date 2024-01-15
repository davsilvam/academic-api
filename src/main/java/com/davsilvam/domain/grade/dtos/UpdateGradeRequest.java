package com.davsilvam.domain.grade.dtos;

import java.util.Optional;
import java.util.UUID;

public record UpdateGradeRequest(Optional<String> name, Optional<Float> value) {
}
