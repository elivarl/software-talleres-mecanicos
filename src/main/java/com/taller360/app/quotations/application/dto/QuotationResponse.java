package com.taller360.app.quotations.application.dto;

import com.taller360.app.quotations.domain.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuotationResponse(
        Long id,
        String code,
        Long workOrderId,
        String workOrderCode,
        String vehiclePlate,
        String customerFullName,
        QuotationStatus status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        String publicToken,
        LocalDateTime sentAt,
        LocalDateTime customerDecisionAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<QuotationItemResponse> items
) {
}
