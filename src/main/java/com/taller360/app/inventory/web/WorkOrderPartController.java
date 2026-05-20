package com.taller360.app.inventory.web;

import com.taller360.app.inventory.application.WorkOrderPartService;
import com.taller360.app.inventory.application.dto.RegisterWorkOrderPartRequest;
import com.taller360.app.inventory.application.dto.WorkOrderPartResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders/{id}/parts")
public class WorkOrderPartController {

    private final WorkOrderPartService workOrderPartService;

    public WorkOrderPartController(WorkOrderPartService workOrderPartService) {
        this.workOrderPartService = workOrderPartService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public WorkOrderPartResponse register(
            @PathVariable("id") Long workOrderId,
            @Valid @RequestBody RegisterWorkOrderPartRequest request
    ) {
        return workOrderPartService.register(workOrderId, request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public List<WorkOrderPartResponse> findByWorkOrderId(@PathVariable("id") Long workOrderId) {
        return workOrderPartService.findByWorkOrderId(workOrderId);
    }

    @DeleteMapping("/{partId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public void delete(@PathVariable("id") Long workOrderId, @PathVariable Long partId) {
        workOrderPartService.delete(workOrderId, partId);
    }
}
