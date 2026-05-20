package com.taller360.app.workorders.application.dto;

import com.taller360.app.workorders.domain.WorkOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkOrderResponse(
        Long id,
        String code,
        Long customerId,
        String customerFullName,
        Long vehicleId,
        String vehiclePlate,
        Long assignedMechanicId,
        String assignedMechanicFullName,
        WorkOrderStatus status,
        LocalDateTime receptionDate,
        LocalDate estimatedDeliveryDate,
        Long currentMileage,
        String fuelLevel,
        String customerComplaint,
        String initialObservations,
        String diagnosis,
        String internalNotes,
        boolean qualityControlCompleted,
        String qualityControlNotes,
        LocalDateTime readyAt,
        LocalDateTime deliveredAt,
        String deliveredTo,
        Long finalMileage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
