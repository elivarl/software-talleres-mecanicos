package com.taller360.app.quotations.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateQuotationRequest(
        @NotNull(message = "Items are required")
        List<@Valid QuotationItemRequest> items
) {
}
