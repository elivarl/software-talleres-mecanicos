package com.taller360.app.inventory.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkOrderPartResponse(
        Long id,
        Long workOrderId,
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
