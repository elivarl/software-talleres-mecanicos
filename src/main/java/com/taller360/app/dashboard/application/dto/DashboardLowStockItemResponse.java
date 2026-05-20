package com.taller360.app.dashboard.application.dto;

import java.math.BigDecimal;

public record DashboardLowStockItemResponse(
        Long id,
        String name,
        String sku,
        BigDecimal currentStock,
        BigDecimal minStock,
        boolean active
) {
}
