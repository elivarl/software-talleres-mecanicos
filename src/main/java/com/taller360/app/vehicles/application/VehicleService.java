package com.taller360.app.vehicles.application;

import com.taller360.app.customers.application.dto.CustomerResponse;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.inspections.domain.InspectionPhoto;
import com.taller360.app.inspections.domain.ReceptionInspection;
import com.taller360.app.inspections.infrastructure.ReceptionInspectionRepository;
import com.taller360.app.inventory.domain.WorkOrderPart;
import com.taller360.app.inventory.infrastructure.WorkOrderPartRepository;
import com.taller360.app.labor.domain.LaborItem;
import com.taller360.app.labor.infrastructure.LaborItemRepository;
import com.taller360.app.quotations.domain.Quotation;
import com.taller360.app.quotations.domain.QuotationItem;
import com.taller360.app.quotations.infrastructure.QuotationRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.vehicles.application.dto.CreateVehicleRequest;
import com.taller360.app.vehicles.application.dto.VehicleHistoryInspectionPhotoResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryInspectionResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryLaborItemResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryOrderTotalsResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryPartResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryQuotationItemResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryQuotationResponse;
import com.taller360.app.vehicles.application.dto.UpdateVehicleRequest;
import com.taller360.app.vehicles.application.dto.VehicleHistoryResponse;
import com.taller360.app.vehicles.application.dto.VehicleHistoryWorkOrderResponse;
import com.taller360.app.vehicles.application.dto.VehicleResponse;
import com.taller360.app.vehicles.domain.Vehicle;
import com.taller360.app.vehicles.infrastructure.VehicleRepository;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ReceptionInspectionRepository receptionInspectionRepository;
    private final QuotationRepository quotationRepository;
    private final WorkOrderPartRepository workOrderPartRepository;
    private final LaborItemRepository laborItemRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            CustomerRepository customerRepository,
            WorkOrderRepository workOrderRepository,
            ReceptionInspectionRepository receptionInspectionRepository,
            QuotationRepository quotationRepository,
            WorkOrderPartRepository workOrderPartRepository,
            LaborItemRepository laborItemRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.workOrderRepository = workOrderRepository;
        this.receptionInspectionRepository = receptionInspectionRepository;
        this.quotationRepository = quotationRepository;
        this.workOrderPartRepository = workOrderPartRepository;
        this.laborItemRepository = laborItemRepository;
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
        List<WorkOrder> workOrders = workOrderRepository.findByVehicleIdOrderByReceptionDateDescCreatedAtDesc(vehicle.getId());
        List<Long> workOrderIds = workOrders.stream()
                .map(WorkOrder::getId)
                .toList();

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

        Map<Long, ReceptionInspection> inspectionsByWorkOrderId = indexInspections(workOrderIds);
        Map<Long, Quotation> quotationsByWorkOrderId = indexQuotations(workOrderIds);
        Map<Long, List<WorkOrderPart>> partsByWorkOrderId = groupParts(workOrderIds);
        Map<Long, List<LaborItem>> laborByWorkOrderId = groupLabor(workOrderIds);

        return new VehicleHistoryResponse(
                toResponse(vehicle),
                customerResponse,
                workOrders.stream()
                        .map(workOrder -> toHistoryWorkOrderResponse(
                                workOrder,
                                inspectionsByWorkOrderId.get(workOrder.getId()),
                                quotationsByWorkOrderId.get(workOrder.getId()),
                                partsByWorkOrderId.getOrDefault(workOrder.getId(), List.of()),
                                laborByWorkOrderId.getOrDefault(workOrder.getId(), List.of())
                        ))
                        .toList()
        );
    }

    private Map<Long, ReceptionInspection> indexInspections(Collection<Long> workOrderIds) {
        if (workOrderIds.isEmpty()) {
            return Map.of();
        }

        return receptionInspectionRepository.findByWorkOrderIdIn(workOrderIds).stream()
                .collect(Collectors.toMap(
                        inspection -> inspection.getWorkOrder().getId(),
                        Function.identity()
                ));
    }

    private Map<Long, Quotation> indexQuotations(Collection<Long> workOrderIds) {
        if (workOrderIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, Quotation> quotationsByWorkOrderId = new LinkedHashMap<>();
        for (Quotation quotation : quotationRepository.findByWorkOrderIdInOrderByCreatedAtDesc(workOrderIds)) {
            quotationsByWorkOrderId.putIfAbsent(quotation.getWorkOrder().getId(), quotation);
        }
        return quotationsByWorkOrderId;
    }

    private Map<Long, List<WorkOrderPart>> groupParts(Collection<Long> workOrderIds) {
        if (workOrderIds.isEmpty()) {
            return Map.of();
        }

        return workOrderPartRepository.findByWorkOrderIdInOrderByCreatedAtAsc(workOrderIds).stream()
                .collect(Collectors.groupingBy(part -> part.getWorkOrder().getId()));
    }

    private Map<Long, List<LaborItem>> groupLabor(Collection<Long> workOrderIds) {
        if (workOrderIds.isEmpty()) {
            return Map.of();
        }

        return laborItemRepository.findByWorkOrderIdInOrderByCreatedAtAsc(workOrderIds).stream()
                .collect(Collectors.groupingBy(laborItem -> laborItem.getWorkOrder().getId()));
    }

    private VehicleHistoryWorkOrderResponse toHistoryWorkOrderResponse(
            WorkOrder workOrder,
            ReceptionInspection inspection,
            Quotation quotation,
            List<WorkOrderPart> usedParts,
            List<LaborItem> laborItems
    ) {
        BigDecimal partsTotal = usedParts.stream()
                .map(WorkOrderPart::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal laborTotal = laborItems.stream()
                .map(LaborItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal serviceTotal = partsTotal.add(laborTotal);

        return new VehicleHistoryWorkOrderResponse(
                workOrder.getId(),
                workOrder.getCode(),
                workOrder.getStatus().name(),
                workOrder.getReceptionDate(),
                workOrder.getEstimatedDeliveryDate(),
                workOrder.getReadyAt(),
                workOrder.getDeliveredAt(),
                workOrder.getDeliveredTo(),
                workOrder.getCurrentMileage(),
                workOrder.getFinalMileage(),
                workOrder.getFuelLevel(),
                workOrder.getCustomerComplaint(),
                workOrder.getInitialObservations(),
                workOrder.getDiagnosis(),
                workOrder.isQualityControlCompleted(),
                workOrder.getQualityControlNotes(),
                toInspectionResponse(inspection),
                toQuotationResponse(quotation),
                usedParts.stream().map(this::toPartResponse).toList(),
                laborItems.stream().map(this::toLaborItemResponse).toList(),
                new VehicleHistoryOrderTotalsResponse(
                        partsTotal,
                        laborTotal,
                        serviceTotal,
                        quotation == null ? null : quotation.getTotal()
                )
        );
    }

    private VehicleHistoryInspectionResponse toInspectionResponse(ReceptionInspection inspection) {
        if (inspection == null) {
            return null;
        }

        return new VehicleHistoryInspectionResponse(
                inspection.getId(),
                inspection.getMileage(),
                inspection.getFuelLevel(),
                inspection.getExteriorCondition(),
                inspection.getVisibleScratches(),
                inspection.getVisibleDents(),
                inspection.getLightsWorking(),
                inspection.getTiresCondition(),
                inspection.getMirrorsCondition(),
                inspection.getHasSpareTire(),
                inspection.getHasJack(),
                inspection.getHasTools(),
                inspection.getHasDocuments(),
                inspection.getPersonalItemsNotes(),
                inspection.getGeneralNotes(),
                inspection.getCreatedAt(),
                inspection.getUpdatedAt(),
                inspection.getPhotos().stream().map(this::toInspectionPhotoResponse).toList()
        );
    }

    private VehicleHistoryInspectionPhotoResponse toInspectionPhotoResponse(InspectionPhoto photo) {
        return new VehicleHistoryInspectionPhotoResponse(
                photo.getId(),
                photo.getPhotoUrl(),
                photo.getDescription(),
                photo.getCreatedAt()
        );
    }

    private VehicleHistoryQuotationResponse toQuotationResponse(Quotation quotation) {
        if (quotation == null) {
            return null;
        }

        return new VehicleHistoryQuotationResponse(
                quotation.getId(),
                quotation.getCode(),
                quotation.getStatus().name(),
                quotation.getSubtotal(),
                quotation.getTax(),
                quotation.getTotal(),
                quotation.getSentAt(),
                quotation.getCustomerDecisionAt(),
                quotation.getItems().stream().map(this::toQuotationItemResponse).toList()
        );
    }

    private VehicleHistoryQuotationItemResponse toQuotationItemResponse(QuotationItem item) {
        return new VehicleHistoryQuotationItemResponse(
                item.getId(),
                item.getType().name(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal()
        );
    }

    private VehicleHistoryPartResponse toPartResponse(WorkOrderPart part) {
        return new VehicleHistoryPartResponse(
                part.getId(),
                part.getInventoryItem().getId(),
                part.getInventoryItem().getName(),
                part.getInventoryItem().getSku(),
                part.getQuantity(),
                part.getUnitCost(),
                part.getSalePrice(),
                part.getTotal(),
                part.getMargin(),
                part.getCreatedAt()
        );
    }

    private VehicleHistoryLaborItemResponse toLaborItemResponse(LaborItem laborItem) {
        return new VehicleHistoryLaborItemResponse(
                laborItem.getId(),
                laborItem.getDescription(),
                laborItem.getPrice(),
                laborItem.getCreatedAt()
        );
    }
}
