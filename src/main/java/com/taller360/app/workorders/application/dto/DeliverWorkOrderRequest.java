package com.taller360.app.workorders.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DeliverWorkOrderRequest(
        @NotBlank(message = "Delivered to is required")
        String deliveredTo,
        @NotNull(message = "Final mileage is required")
        @PositiveOrZero(message = "Final mileage must be greater than or equal to 0")
        Long finalMileage
) {
}
