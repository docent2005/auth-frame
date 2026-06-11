package org.example.authcore.dto;

import java.util.Collection;

public record LoginResponse(
        String message,
        String username,
        Collection<?> authorities
) {
}
