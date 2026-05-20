package com.taller360.app.workorders.web;

import com.taller360.app.workorders.application.WorkOrderService;
import com.taller360.app.workorders.application.dto.AssignMechanicRequest;
import com.taller360.app.workorders.application.dto.CreateWorkOrderRequest;
import com.taller360.app.workorders.application.dto.UpdateDiagnosisRequest;
import com.taller360.app.workorders.application.dto.UpdateInternalNotesRequest;
import com.taller360.app.workorders.application.dto.UpdateQualityControlRequest;
import com.taller360.app.workorders.application.dto.UpdateWorkOrderStatusRequest;
import com.taller360.app.workorders.application.dto.WorkOrderResponse;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public List<WorkOrderResponse> findAll(
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false) String plate,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receptionDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receptionDateTo
    ) {
        return workOrderService.findAll(status, plate, customerId, receptionDateFrom, receptionDateTo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public WorkOrderResponse create(@Valid @RequestBody CreateWorkOrderRequest request) {
        return workOrderService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public WorkOrderResponse findById(@PathVariable Long id) {
        return workOrderService.findById(id);
    }

    @PatchMapping("/{id}/assign-mechanic")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public WorkOrderResponse assignMechanic(@PathVariable Long id, @Valid @RequestBody AssignMechanicRequest request) {
        return workOrderService.assignMechanic(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public WorkOrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateWorkOrderStatusRequest request) {
        return workOrderService.updateStatus(id, request);
    }

    @PatchMapping("/{id}/diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public WorkOrderResponse registerDiagnosis(@PathVariable Long id, @Valid @RequestBody UpdateDiagnosisRequest request) {
        return workOrderService.registerDiagnosis(id, request);
    }

    @PatchMapping("/{id}/internal-notes")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public WorkOrderResponse updateInternalNotes(@PathVariable Long id, @Valid @RequestBody UpdateInternalNotesRequest request) {
        return workOrderService.updateInternalNotes(id, request);
    }

    @PatchMapping("/{id}/quality-control")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public WorkOrderResponse updateQualityControl(@PathVariable Long id, @Valid @RequestBody UpdateQualityControlRequest request) {
        return workOrderService.updateQualityControl(id, request);
    }

    @PatchMapping("/{id}/mark-ready")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public WorkOrderResponse markReady(@PathVariable Long id) {
        return workOrderService.markReady(id);
    }
}
