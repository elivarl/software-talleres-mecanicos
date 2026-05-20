package com.taller360.app.inspections.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReceptionInspectionResponse(
        Long id,
        Long workOrderId,
        String workOrderCode,
        Long mileage,
        String fuelLevel,
        String exteriorCondition,
        String visibleScratches,
        String visibleDents,
        Boolean lightsWorking,
        String tiresCondition,
        String mirrorsCondition,
        Boolean hasSpareTire,
        Boolean hasJack,
        Boolean hasTools,
        Boolean hasDocuments,
        String personalItemsNotes,
        String generalNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<InspectionPhotoResponse> photos
) {
}
