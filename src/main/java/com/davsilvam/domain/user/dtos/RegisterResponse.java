package com.davsilvam.domain.user.dtos;

import java.util.UUID;

public record RegisterResponse(UUID id, String name, String email) {
}
