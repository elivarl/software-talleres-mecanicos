package com.taller360.app.vehicles.application.dto;

import java.time.LocalDateTime;

public record VehicleHistoryInspectionPhotoResponse(
        Long id,
        String photoUrl,
        String description,
        LocalDateTime createdAt
) {
}
