package com.taller360.app.vehicles.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VehicleHistoryQuotationResponse(
        Long id,
        String code,
        String status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        LocalDateTime sentAt,
        LocalDateTime customerDecisionAt,
        List<VehicleHistoryQuotationItemResponse> items
) {
}
