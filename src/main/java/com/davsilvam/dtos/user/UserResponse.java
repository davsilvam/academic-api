package com.davsilvam.dtos.user;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email) {
}
