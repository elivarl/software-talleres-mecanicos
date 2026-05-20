package com.taller360.app.inventory.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryItemResponse(
        Long id,
        String name,
        String sku,
        String description,
        BigDecimal currentStock,
        BigDecimal minStock,
        BigDecimal unitCost,
        BigDecimal salePrice,
        boolean active,
        boolean lowStock,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
