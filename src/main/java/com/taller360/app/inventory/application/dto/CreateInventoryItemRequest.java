package com.taller360.app.inventory.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateInventoryItemRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,
        @NotBlank(message = "SKU is required")
        @Size(max = 80, message = "SKU must not exceed 80 characters")
        String sku,
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,
        @NotNull(message = "Current stock is required")
        @DecimalMin(value = "0.00", message = "Current stock must be greater than or equal to 0")
        BigDecimal currentStock,
        @NotNull(message = "Min stock is required")
        @DecimalMin(value = "0.00", message = "Min stock must be greater than or equal to 0")
        BigDecimal minStock,
        @NotNull(message = "Unit cost is required")
        @DecimalMin(value = "0.00", message = "Unit cost must be greater than or equal to 0")
        BigDecimal unitCost,
        @NotNull(message = "Sale price is required")
        @DecimalMin(value = "0.00", message = "Sale price must be greater than or equal to 0")
        BigDecimal salePrice,
        Boolean active
) {
}
