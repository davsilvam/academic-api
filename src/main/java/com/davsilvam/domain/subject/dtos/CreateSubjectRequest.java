package com.davsilvam.domain.subject.dtos;

import java.util.List;
import java.util.UUID;

public record CreateSubjectRequest(String name, String description, List<UUID> professors_ids) {
}
