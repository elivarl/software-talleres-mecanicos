package com.taller360.app.workorders.application.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateQualityControlRequest(
        @NotNull(message = "Completed flag is required")
        Boolean completed,
        String notes
) {
}
