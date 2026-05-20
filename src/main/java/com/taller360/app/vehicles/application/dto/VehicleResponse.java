package com.taller360.app.vehicles.application.dto;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        Long customerId,
        String customerFullName,
        String plate,
        String brand,
        String model,
        Integer year,
        String color,
        String vin,
        Long mileage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
