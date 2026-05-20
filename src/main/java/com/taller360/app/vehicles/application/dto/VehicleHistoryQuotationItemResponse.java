package com.taller360.app.vehicles.application.dto;

import java.math.BigDecimal;

public record VehicleHistoryQuotationItemResponse(
        Long id,
        String type,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {
}
