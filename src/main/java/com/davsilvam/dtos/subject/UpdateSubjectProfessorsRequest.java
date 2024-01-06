package com.davsilvam.dtos.subject;

import java.util.List;
import java.util.UUID;

public record UpdateSubjectProfessorsRequest(List<UUID> professors_ids) {
}
