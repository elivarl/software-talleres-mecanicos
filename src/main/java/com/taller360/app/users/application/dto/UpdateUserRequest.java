package com.taller360.app.users.application.dto;

import com.taller360.app.users.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Full name is required")
        String fullName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password,
        @NotNull(message = "Role is required")
        UserRole role,
        @NotNull(message = "Active status is required")
        Boolean active
) {
}
