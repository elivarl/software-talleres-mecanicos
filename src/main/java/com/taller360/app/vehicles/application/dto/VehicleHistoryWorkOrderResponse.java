package com.taller360.app.vehicles.application.dto;

import java.time.LocalDateTime;

public record VehicleHistoryWorkOrderResponse(
        Long workOrderId,
        String workOrderCode,
        LocalDateTime serviceDate,
        String status
) {
}
