package com.taller360.app.inspections.application.dto;

import java.time.LocalDateTime;

public record InspectionPhotoResponse(
        Long id,
        String photoUrl,
        String description,
        LocalDateTime createdAt
) {
}
