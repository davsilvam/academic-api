package com.davsilvam.domain.professor.dtos;

import java.util.Optional;

public record UpdateProfessorRequest(Optional<String> name, Optional<String> email) {
}
