package com.taller360.app.labor.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LaborItemResponse(
        Long id,
        Long workOrderId,
        String description,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
