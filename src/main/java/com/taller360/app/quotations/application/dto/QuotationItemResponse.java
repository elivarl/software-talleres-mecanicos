package com.taller360.app.quotations.application.dto;

import com.taller360.app.quotations.domain.QuotationItemType;

import java.math.BigDecimal;

public record QuotationItemResponse(
        Long id,
        QuotationItemType type,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {
}
