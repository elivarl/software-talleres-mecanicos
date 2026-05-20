package com.taller360.app.workorders.application;

import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.users.domain.User;
import com.taller360.app.users.domain.UserRole;
import com.taller360.app.users.infrastructure.UserRepository;
import com.taller360.app.vehicles.domain.Vehicle;
import com.taller360.app.vehicles.infrastructure.VehicleRepository;
import com.taller360.app.workorders.application.dto.AssignMechanicRequest;
import com.taller360.app.workorders.application.dto.CreateWorkOrderRequest;
import com.taller360.app.workorders.application.dto.UpdateDiagnosisRequest;
import com.taller360.app.workorders.application.dto.UpdateInternalNotesRequest;
import com.taller360.app.workorders.application.dto.UpdateWorkOrderStatusRequest;
import com.taller360.app.workorders.application.dto.WorkOrderResponse;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderSequence;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import com.taller360.app.workorders.infrastructure.WorkOrderSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderSequenceRepository workOrderSequenceRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            WorkOrderSequenceRepository workOrderSequenceRepository,
            CustomerRepository customerRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderSequenceRepository = workOrderSequenceRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> findAll(
            WorkOrderStatus status,
            String plate,
            Long customerId,
            LocalDate receptionDateFrom,
            LocalDate receptionDateTo
    ) {
        LocalDateTime from = receptionDateFrom == null ? null : receptionDateFrom.atStartOfDay();
        LocalDateTime to = receptionDateTo == null ? null : receptionDateTo.atTime(LocalTime.MAX);

        return workOrderRepository.search(
                        status,
                        normalizeNullable(plate),
                        customerId,
                        from,
                        to
                ).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse findById(Long id) {
        return toResponse(getWorkOrderEntity(id));
    }

    @Transactional
    public WorkOrderResponse create(CreateWorkOrderRequest request) {
        Customer customer = getCustomer(request.customerId());
        Vehicle vehicle = getVehicle(request.vehicleId());
        validateVehicleOwnership(customer, vehicle);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode(generateCode());
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(WorkOrderStatus.RECEIVED);
        workOrder.setReceptionDate(request.receptionDate());
        workOrder.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
        workOrder.setCurrentMileage(request.currentMileage());
        workOrder.setFuelLevel(normalizeNullable(request.fuelLevel()));
        workOrder.setCustomerComplaint(request.customerComplaint().trim());
        workOrder.setInitialObservations(normalizeNullable(request.initialObservations()));
        workOrder.setQualityControlCompleted(false);

        if (request.assignedMechanicId() != null) {
            workOrder.setAssignedMechanic(getMechanic(request.assignedMechanicId()));
        }

        return toResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public WorkOrderResponse assignMechanic(Long id, AssignMechanicRequest request) {
        WorkOrder workOrder = getWorkOrderEntity(id);
        workOrder.assignMechanic(getMechanic(request.assignedMechanicId()));
        return toResponse(workOrder);
    }

    @Transactional
    public WorkOrderResponse updateStatus(Long id, UpdateWorkOrderStatusRequest request) {
        WorkOrder workOrder = getWorkOrderEntity(id);
        workOrder.updateStatus(request.status());
        return toResponse(workOrder);
    }

    @Transactional
    public WorkOrderResponse registerDiagnosis(Long id, UpdateDiagnosisRequest request) {
        WorkOrder workOrder = getWorkOrderEntity(id);
        workOrder.registerDiagnosis(request.diagnosis().trim());
        return toResponse(workOrder);
    }

    @Transactional
    public WorkOrderResponse updateInternalNotes(Long id, UpdateInternalNotesRequest request) {
        WorkOrder workOrder = getWorkOrderEntity(id);
        workOrder.updateInternalNotes(request.internalNotes().trim());
        return toResponse(workOrder);
    }

    @Transactional(readOnly = true)
    public WorkOrder getWorkOrderEntity(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
    }

    private String generateCode() {
        WorkOrderSequence sequence = workOrderSequenceRepository.save(new WorkOrderSequence());
        return "OT-%06d".formatted(sequence.getId());
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private Vehicle getVehicle(Long id) {
        return vehicleRepository.findWithCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    private User getMechanic(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.MECHANIC) {
            throw new BusinessRuleException("Assigned mechanic must have MECHANIC role");
        }

        if (!user.isActive()) {
            throw new BusinessRuleException("Assigned mechanic must be active");
        }

        return user;
    }

    private void validateVehicleOwnership(Customer customer, Vehicle vehicle) {
        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException("Vehicle does not belong to the provided customer");
        }
    }

    private String normalizeNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private WorkOrderResponse toResponse(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getCode(),
                workOrder.getCustomer().getId(),
                workOrder.getCustomer().getFullName(),
                workOrder.getVehicle().getId(),
                workOrder.getVehicle().getPlate(),
                workOrder.getAssignedMechanic() == null ? null : workOrder.getAssignedMechanic().getId(),
                workOrder.getAssignedMechanic() == null ? null : workOrder.getAssignedMechanic().getFullName(),
                workOrder.getStatus(),
                workOrder.getReceptionDate(),
                workOrder.getEstimatedDeliveryDate(),
                workOrder.getCurrentMileage(),
                workOrder.getFuelLevel(),
                workOrder.getCustomerComplaint(),
                workOrder.getInitialObservations(),
                workOrder.getDiagnosis(),
                workOrder.getInternalNotes(),
                workOrder.isQualityControlCompleted(),
                workOrder.getQualityControlNotes(),
                workOrder.getReadyAt(),
                workOrder.getDeliveredAt(),
                workOrder.getDeliveredTo(),
                workOrder.getFinalMileage(),
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt()
        );
    }
}
