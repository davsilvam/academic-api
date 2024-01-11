package com.davsilvam.dtos.grade;

import java.util.UUID;

public record CreateGradeRequest(String name, Float value, UUID subject_id) {
}
