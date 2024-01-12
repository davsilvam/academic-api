package com.davsilvam.dtos.grade;

import java.util.Optional;
import java.util.UUID;

public record UpdateGradeRequest(Optional<String> name, Optional<Float> value) {
}
