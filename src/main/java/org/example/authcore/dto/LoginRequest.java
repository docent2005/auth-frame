package org.example.authcore.dto;

public record LoginRequest(
        String username,
        String password
) {
}
