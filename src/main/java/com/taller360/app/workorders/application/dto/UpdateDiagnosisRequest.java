package com.taller360.app.workorders.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDiagnosisRequest(
        @NotBlank(message = "Diagnosis is required")
        String diagnosis
) {
}
