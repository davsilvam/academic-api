package com.davsilvam.domain.subject.dtos;

import java.util.List;
import java.util.UUID;

public record UpdateSubjectProfessorsRequest(List<UUID> professors_ids) {
}
