package com.taller360.app.vehicles.application;

import com.taller360.app.customers.application.dto.CustomerResponse;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.vehicles.application.dto.CreateVehicleRequest;
import com.taller360.app.vehicles.application.dto.UpdateVehicleRequest;
import com.taller360.app.vehicles.application.dto.VehicleHistoryResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryWorkOrderResponse;
import com.taller360.app.vehicles.application.dto.VehicleResponse;
import com.taller360.app.vehicles.domain.Vehicle;
import com.taller360.app.vehicles.infrastructure.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    public VehicleService(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll(String plate) {
        List<Vehicle> vehicles = hasText(plate)
                ? vehicleRepository.findByPlateContainingIgnoreCaseOrderByPlateAsc(plate.trim())
                : vehicleRepository.findAll().stream().sorted((left, right) -> left.getPlate().compareToIgnoreCase(right.getPlate())).toList();

        return vehicles.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {
        return toResponse(getVehicleEntityWithCustomer(id));
    }

    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        String normalizedPlate = normalizePlate(request.plate());
        validatePlateAvailability(normalizedPlate, null);
        validateYear(request.year());

        Vehicle vehicle = new Vehicle();
        applyChanges(
                vehicle,
                getCustomerEntity(request.customerId()),
                normalizedPlate,
                request.brand(),
                request.model(),
                request.year(),
                request.color(),
                request.vin(),
                request.mileage()
        );

        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = getVehicleEntityWithCustomer(id);
        String normalizedPlate = normalizePlate(request.plate());
        validatePlateAvailability(normalizedPlate, id);
        validateYear(request.year());

        applyChanges(
                vehicle,
                getCustomerEntity(request.customerId()),
                normalizedPlate,
                request.brand(),
                request.model(),
                request.year(),
                request.color(),
                request.vin(),
                request.mileage()
        );

        return toResponse(vehicle);
    }

    @Transactional(readOnly = true)
    public VehicleHistoryResponse findHistoryById(Long id) {
        return toHistoryResponse(getVehicleEntityWithCustomer(id));
    }

    @Transactional(readOnly = true)
    public VehicleHistoryResponse findHistoryByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findWithCustomerByPlateIgnoreCase(normalizePlate(plate))
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return toHistoryResponse(vehicle);
    }

    private void applyChanges(
            Vehicle vehicle,
            Customer customer,
            String plate,
            String brand,
            String model,
            Integer year,
            String color,
            String vin,
            Long mileage
    ) {
        vehicle.setCustomer(customer);
        vehicle.setPlate(plate);
        vehicle.setBrand(brand.trim());
        vehicle.setModel(model.trim());
        vehicle.setYear(year);
        vehicle.setColor(normalizeNullable(color));
        vehicle.setVin(normalizeNullable(vin));
        vehicle.setMileage(mileage);
    }

    private Vehicle getVehicleEntityWithCustomer(Long id) {
        return vehicleRepository.findWithCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    private Customer getCustomerEntity(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void validatePlateAvailability(String plate, Long currentVehicleId) {
        vehicleRepository.findByPlateIgnoreCase(plate)
                .filter(existingVehicle -> !existingVehicle.getId().equals(currentVehicleId))
                .ifPresent(existingVehicle -> {
                    throw new BusinessRuleException("Plate is already registered");
                });
    }

    private void validateYear(Integer year) {
        if (year == null) {
            return;
        }

        int maxYear = java.time.Year.now().getValue() + 1;
        if (year < 1900 || year > maxYear) {
            throw new BusinessRuleException("Year must be between 1900 and " + maxYear);
        }
    }

    private String normalizePlate(String plate) {
        return plate.trim().toUpperCase();
    }

    private String normalizeNullable(String value) {
        if (!hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getCustomer().getId(),
                vehicle.getCustomer().getFullName(),
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getVin(),
                vehicle.getMileage(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }

    private VehicleHistoryResponse toHistoryResponse(Vehicle vehicle) {
        Customer customer = vehicle.getCustomer();

        CustomerResponse customerResponse = new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getIdentification(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );

        return new VehicleHistoryResponse(
                toResponse(vehicle),
                customerResponse,
                List.<VehicleHistoryWorkOrderResponse>of()
        );
    }
}
