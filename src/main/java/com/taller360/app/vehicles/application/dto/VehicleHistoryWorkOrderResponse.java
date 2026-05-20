package com.taller360.app.vehicles.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record VehicleHistoryWorkOrderResponse(
        Long workOrderId,
        String workOrderCode,
        String status,
        LocalDateTime receptionDate,
        LocalDate estimatedDeliveryDate,
        LocalDateTime readyAt,
        LocalDateTime deliveredAt,
        String deliveredTo,
        Long currentMileage,
        Long finalMileage,
        String fuelLevel,
        String customerComplaint,
        String initialObservations,
        String diagnosis,
        boolean qualityControlCompleted,
        String qualityControlNotes,
        VehicleHistoryInspectionResponse inspection,
        VehicleHistoryQuotationResponse quotation,
        List<VehicleHistoryPartResponse> usedParts,
        List<VehicleHistoryLaborItemResponse> laborItems,
        VehicleHistoryOrderTotalsResponse totals
) {
}
