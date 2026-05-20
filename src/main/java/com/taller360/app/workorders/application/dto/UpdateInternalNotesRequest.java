package com.taller360.app.workorders.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateInternalNotesRequest(
        @NotBlank(message = "Internal notes are required")
        String internalNotes
) {
}
