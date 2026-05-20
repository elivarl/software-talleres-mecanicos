package com.taller360.app.workorders.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateWorkOrderRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,
        @NotNull(message = "Vehicle id is required")
        Long vehicleId,
        Long assignedMechanicId,
        LocalDateTime receptionDate,
        LocalDate estimatedDeliveryDate,
        @NotNull(message = "Current mileage is required")
        @Min(value = 0, message = "Current mileage must be greater than or equal to 0")
        Long currentMileage,
        @Size(max = 50, message = "Fuel level must not exceed 50 characters")
        String fuelLevel,
        @NotBlank(message = "Customer complaint is required")
        String customerComplaint,
        String initialObservations
) {
}
