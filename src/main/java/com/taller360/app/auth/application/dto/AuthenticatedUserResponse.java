package com.taller360.app.auth.application.dto;

import com.taller360.app.users.domain.UserRole;

public record AuthenticatedUserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role
) {
}
