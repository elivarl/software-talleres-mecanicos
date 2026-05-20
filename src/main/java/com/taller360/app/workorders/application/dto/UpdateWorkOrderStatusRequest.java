package com.taller360.app.workorders.application.dto;

import com.taller360.app.workorders.domain.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateWorkOrderStatusRequest(
        @NotNull(message = "Status is required")
        WorkOrderStatus status
) {
}
