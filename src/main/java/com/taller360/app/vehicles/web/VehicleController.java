package com.taller360.app.vehicles.web;

import com.taller360.app.vehicles.application.VehicleService;
import com.taller360.app.vehicles.application.dto.CreateVehicleRequest;
import com.taller360.app.vehicles.application.dto.UpdateVehicleRequest;
import com.taller360.app.vehicles.application.dto.VehicleHistoryResponse;
import com.taller360.app.vehicles.application.dto.VehicleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<VehicleResponse> findAll(@RequestParam(required = false) String plate) {
        return vehicleService.findAll(plate);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(@Valid @RequestBody CreateVehicleRequest request) {
        return vehicleService.create(request);
    }

    @GetMapping("/{id}")
    public VehicleResponse findById(@PathVariable Long id) {
        return vehicleService.findById(id);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody UpdateVehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @GetMapping("/{id}/history")
    public VehicleHistoryResponse findHistoryById(@PathVariable Long id) {
        return vehicleService.findHistoryById(id);
    }

    @GetMapping("/by-plate/{plate}/history")
    public VehicleHistoryResponse findHistoryByPlate(@PathVariable String plate) {
        return vehicleService.findHistoryByPlate(plate);
    }
}
