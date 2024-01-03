package com.davsilvam.dtos.subject;

import java.util.UUID;

public record SubjectResponse(UUID id, String name, String description, UUID userId) {
}
