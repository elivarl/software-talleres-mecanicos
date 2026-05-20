package com.taller360.app.customers.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 150, message = "Full name must not exceed 150 characters")
        String fullName,
        @Size(max = 50, message = "Identification must not exceed 50 characters")
        String identification,
        @NotBlank(message = "Phone is required")
        @Size(max = 50, message = "Phone must not exceed 50 characters")
        String phone,
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address
) {
}
