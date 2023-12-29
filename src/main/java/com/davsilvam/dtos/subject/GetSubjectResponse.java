package com.davsilvam.dtos.subject;

import java.util.UUID;

public record GetSubjectResponse(UUID id, String name, String description, UUID userId) {
}
