package com.taller360.app.inspections.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReceptionInspectionRequest(
        @NotNull(message = "Mileage is required")
        @Min(value = 0, message = "Mileage must be greater than or equal to 0")
        Long mileage,
        @Size(max = 50, message = "Fuel level must not exceed 50 characters")
        String fuelLevel,
        @Size(max = 255, message = "Exterior condition must not exceed 255 characters")
        String exteriorCondition,
        String visibleScratches,
        String visibleDents,
        Boolean lightsWorking,
        @Size(max = 255, message = "Tires condition must not exceed 255 characters")
        String tiresCondition,
        @Size(max = 255, message = "Mirrors condition must not exceed 255 characters")
        String mirrorsCondition,
        Boolean hasSpareTire,
        Boolean hasJack,
        Boolean hasTools,
        Boolean hasDocuments,
        String personalItemsNotes,
        String generalNotes
) {
}
