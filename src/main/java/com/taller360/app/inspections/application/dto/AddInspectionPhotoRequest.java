package com.taller360.app.inspections.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddInspectionPhotoRequest(
        @NotBlank(message = "Photo URL is required")
        @Size(max = 500, message = "Photo URL must not exceed 500 characters")
        String photoUrl,
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description
) {
}
