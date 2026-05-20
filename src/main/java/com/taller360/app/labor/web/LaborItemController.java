package com.taller360.app.labor.web;

import com.taller360.app.labor.application.LaborItemService;
import com.taller360.app.labor.application.dto.LaborItemResponse;
import com.taller360.app.labor.application.dto.RegisterLaborItemRequest;
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
@RequestMapping("/api/work-orders/{id}/labor")
public class LaborItemController {

    private final LaborItemService laborItemService;

    public LaborItemController(LaborItemService laborItemService) {
        this.laborItemService = laborItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public LaborItemResponse register(
            @PathVariable("id") Long workOrderId,
            @Valid @RequestBody RegisterLaborItemRequest request
    ) {
        return laborItemService.register(workOrderId, request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MECHANIC')")
    public List<LaborItemResponse> findByWorkOrderId(@PathVariable("id") Long workOrderId) {
        return laborItemService.findByWorkOrderId(workOrderId);
    }

    @DeleteMapping("/{laborId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public void delete(@PathVariable("id") Long workOrderId, @PathVariable Long laborId) {
        laborItemService.delete(workOrderId, laborId);
    }
}
