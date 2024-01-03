package com.davsilvam.dtos.subject;

import java.util.Optional;

public record UpdateSubjectRequest(Optional<String> name, Optional<String> description) {
}
