package com.taller360.app.inventory.web;

import com.taller360.app.inventory.application.InventoryService;
import com.taller360.app.inventory.application.dto.CreateInventoryItemRequest;
import com.taller360.app.inventory.application.dto.InventoryItemResponse;
import com.taller360.app.inventory.application.dto.UpdateInventoryItemRequest;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryItemResponse> findAll(@RequestParam(required = false) String search) {
        return inventoryService.findAll(search);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryItemResponse create(@Valid @RequestBody CreateInventoryItemRequest request) {
        return inventoryService.create(request);
    }

    @GetMapping("/{id}")
    public InventoryItemResponse findById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @PutMapping("/{id}")
    public InventoryItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateInventoryItemRequest request) {
        return inventoryService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public InventoryItemResponse deactivate(@PathVariable Long id) {
        return inventoryService.deactivate(id);
    }

    @GetMapping("/low-stock")
    public List<InventoryItemResponse> findLowStockItems() {
        return inventoryService.findLowStockItems();
    }
}
