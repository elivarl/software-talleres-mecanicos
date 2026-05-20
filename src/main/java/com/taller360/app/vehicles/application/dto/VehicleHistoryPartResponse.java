package com.taller360.app.vehicles.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VehicleHistoryPartResponse(
        Long id,
        Long inventoryItemId,
        String inventoryItemName,
        String inventoryItemSku,
        BigDecimal quantity,
        BigDecimal unitCost,
        BigDecimal salePrice,
        BigDecimal total,
        BigDecimal margin,
        LocalDateTime createdAt
) {
}
