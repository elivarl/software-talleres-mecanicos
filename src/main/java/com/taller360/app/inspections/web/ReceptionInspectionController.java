package com.taller360.app.inspections.web;

import com.taller360.app.inspections.application.ReceptionInspectionService;
import com.taller360.app.inspections.application.dto.AddInspectionPhotoRequest;
import com.taller360.app.inspections.application.dto.CreateReceptionInspectionRequest;
import com.taller360.app.inspections.application.dto.ReceptionInspectionResponse;
import com.taller360.app.inspections.application.dto.UpdateReceptionInspectionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceptionInspectionController {

    private final ReceptionInspectionService receptionInspectionService;

    public ReceptionInspectionController(ReceptionInspectionService receptionInspectionService) {
        this.receptionInspectionService = receptionInspectionService;
    }

    @PostMapping("/api/work-orders/{id}/inspection")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ReceptionInspectionResponse create(
            @PathVariable Long id,
            @Valid @RequestBody CreateReceptionInspectionRequest request
    ) {
        return receptionInspectionService.create(id, request);
    }

    @GetMapping("/api/work-orders/{id}/inspection")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public ReceptionInspectionResponse findByWorkOrderId(@PathVariable Long id) {
        return receptionInspectionService.findByWorkOrderId(id);
    }

    @PutMapping("/api/inspections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ReceptionInspectionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReceptionInspectionRequest request
    ) {
        return receptionInspectionService.update(id, request);
    }

    @PostMapping("/api/inspections/{id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ReceptionInspectionResponse addPhoto(
            @PathVariable Long id,
            @Valid @RequestBody AddInspectionPhotoRequest request
    ) {
        return receptionInspectionService.addPhoto(id, request);
    }
}
