package com.davsilvam.domain.subject.dtos;

import java.util.Optional;

public record UpdateSubjectRequest(Optional<String> name, Optional<String> description) {
}
