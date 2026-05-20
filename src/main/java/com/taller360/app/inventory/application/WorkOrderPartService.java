package com.taller360.app.inventory.application;

import com.taller360.app.inventory.application.dto.RegisterWorkOrderPartRequest;
import com.taller360.app.inventory.application.dto.WorkOrderPartResponse;
import com.taller360.app.inventory.domain.InventoryItem;
import com.taller360.app.inventory.domain.WorkOrderPart;
import com.taller360.app.inventory.infrastructure.WorkOrderPartRepository;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class WorkOrderPartService {

    private final WorkOrderPartRepository workOrderPartRepository;
    private final WorkOrderRepository workOrderRepository;
    private final InventoryService inventoryService;

    public WorkOrderPartService(
            WorkOrderPartRepository workOrderPartRepository,
            WorkOrderRepository workOrderRepository,
            InventoryService inventoryService
    ) {
        this.workOrderPartRepository = workOrderPartRepository;
        this.workOrderRepository = workOrderRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public WorkOrderPartResponse register(Long workOrderId, RegisterWorkOrderPartRequest request) {
        WorkOrder workOrder = getWorkOrder(workOrderId);
        workOrder.ensureAllowsPartRegistration();

        InventoryItem inventoryItem = inventoryService.getInventoryItemEntity(request.inventoryItemId());
        inventoryItem.ensureActiveForNewWorkOrderUse();

        BigDecimal quantity = scale(request.quantity());
        inventoryItem.reduceStock(quantity);

        WorkOrderPart workOrderPart = new WorkOrderPart();
        workOrderPart.setWorkOrder(workOrder);
        workOrderPart.setInventoryItem(inventoryItem);
        workOrderPart.setQuantity(quantity);
        workOrderPart.setUnitCost(inventoryItem.getUnitCost());
        workOrderPart.setSalePrice(inventoryItem.getSalePrice());
        workOrderPart.recalculateAmounts();

        return toResponse(workOrderPartRepository.save(workOrderPart));
    }

    @Transactional(readOnly = true)
    public List<WorkOrderPartResponse> findByWorkOrderId(Long workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException("Work order not found");
        }

        return workOrderPartRepository.findByWorkOrderIdOrderByCreatedAtAsc(workOrderId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long workOrderId, Long partId) {
        WorkOrderPart workOrderPart = workOrderPartRepository.findByIdAndWorkOrderId(partId, workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Used part not found"));

        WorkOrder workOrder = workOrderPart.getWorkOrder();
        workOrder.ensureAllowsPartDeletion();

        workOrderPart.getInventoryItem().restoreStock(workOrderPart.getQuantity());
        workOrderPartRepository.delete(workOrderPart);
    }

    private WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private WorkOrderPartResponse toResponse(WorkOrderPart workOrderPart) {
        return new WorkOrderPartResponse(
                workOrderPart.getId(),
                workOrderPart.getWorkOrder().getId(),
                workOrderPart.getInventoryItem().getId(),
                workOrderPart.getInventoryItem().getName(),
                workOrderPart.getInventoryItem().getSku(),
                workOrderPart.getQuantity(),
                workOrderPart.getUnitCost(),
                workOrderPart.getSalePrice(),
                workOrderPart.getTotal(),
                workOrderPart.getMargin(),
                workOrderPart.getCreatedAt()
        );
    }
}
