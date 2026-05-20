package com.taller360.app.inventory.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterWorkOrderPartRequest(
        @NotNull(message = "Inventory item id is required")
        Long inventoryItemId,
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        BigDecimal quantity
) {
}
