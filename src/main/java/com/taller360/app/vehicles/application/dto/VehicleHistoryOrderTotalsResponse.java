package com.taller360.app.vehicles.application.dto;

import java.math.BigDecimal;

public record VehicleHistoryOrderTotalsResponse(
        BigDecimal partsTotal,
        BigDecimal laborTotal,
        BigDecimal serviceTotal,
        BigDecimal quotationTotal
) {
}
