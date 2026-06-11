package org.example.authcore.dto;

import java.util.Collection;

public record CurrentUserResponse(
        String username,
        Collection<?> authorities,
        boolean authenticated
) {
}