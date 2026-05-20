package com.taller360.app.labor.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterLaborItemRequest(
        @NotBlank(message = "Description is required")
        String description,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must be greater than or equal to 0")
        BigDecimal price
) {
}
