package com.davsilvam.dtos.authentication;

import java.util.UUID;

public record RegisterResponse(UUID id, String name, String email) {
}
