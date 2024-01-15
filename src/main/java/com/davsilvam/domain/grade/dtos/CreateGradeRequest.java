package com.davsilvam.domain.grade.dtos;

import java.util.UUID;

public record CreateGradeRequest(String name, Float value, UUID subject_id) {
}
