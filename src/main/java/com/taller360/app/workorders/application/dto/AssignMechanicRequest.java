package com.taller360.app.workorders.application.dto;

import jakarta.validation.constraints.NotNull;

public record AssignMechanicRequest(
        @NotNull(message = "Assigned mechanic id is required")
        Long assignedMechanicId
) {
}
