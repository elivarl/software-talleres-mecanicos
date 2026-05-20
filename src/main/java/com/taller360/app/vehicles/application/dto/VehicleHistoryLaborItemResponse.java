package com.taller360.app.vehicles.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VehicleHistoryLaborItemResponse(
        Long id,
        String description,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
