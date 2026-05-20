package com.taller360.app.vehicles.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateVehicleRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,
        @NotBlank(message = "Plate is required")
        @Size(max = 20, message = "Plate must not exceed 20 characters")
        String plate,
        @NotBlank(message = "Brand is required")
        @Size(max = 100, message = "Brand must not exceed 100 characters")
        String brand,
        @NotBlank(message = "Model is required")
        @Size(max = 100, message = "Model must not exceed 100 characters")
        String model,
        Integer year,
        @Size(max = 50, message = "Color must not exceed 50 characters")
        String color,
        @Size(max = 100, message = "VIN must not exceed 100 characters")
        String vin,
        @NotNull(message = "Mileage is required")
        @Min(value = 0, message = "Mileage must be greater than or equal to 0")
        Long mileage
) {
}
