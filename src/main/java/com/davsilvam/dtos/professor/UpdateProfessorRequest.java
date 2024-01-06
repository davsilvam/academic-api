package com.davsilvam.dtos.professor;

import java.util.Optional;

public record UpdateProfessorRequest(Optional<String> name, Optional<String> email) {
}
