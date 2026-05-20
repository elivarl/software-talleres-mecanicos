package com.taller360.app.labor.application;

import com.taller360.app.labor.application.dto.LaborItemResponse;
import com.taller360.app.labor.application.dto.RegisterLaborItemRequest;
import com.taller360.app.labor.domain.LaborItem;
import com.taller360.app.labor.infrastructure.LaborItemRepository;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LaborItemService {

    private final LaborItemRepository laborItemRepository;
    private final WorkOrderRepository workOrderRepository;

    public LaborItemService(LaborItemRepository laborItemRepository, WorkOrderRepository workOrderRepository) {
        this.laborItemRepository = laborItemRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public LaborItemResponse register(Long workOrderId, RegisterLaborItemRequest request) {
        WorkOrder workOrder = getWorkOrder(workOrderId);
        workOrder.ensureAllowsLaborRegistration();

        LaborItem laborItem = new LaborItem();
        laborItem.setWorkOrder(workOrder);
        laborItem.setDescription(request.description().trim());
        laborItem.setPrice(scale(request.price()));

        return toResponse(laborItemRepository.save(laborItem));
    }

    @Transactional(readOnly = true)
    public List<LaborItemResponse> findByWorkOrderId(Long workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException("Work order not found");
        }

        return laborItemRepository.findByWorkOrderIdOrderByCreatedAtAsc(workOrderId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long workOrderId, Long laborItemId) {
        LaborItem laborItem = laborItemRepository.findByIdAndWorkOrderId(laborItemId, workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Labor item not found"));

        laborItem.getWorkOrder().ensureAllowsLaborDeletion();
        laborItemRepository.delete(laborItem);
    }

    private WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private LaborItemResponse toResponse(LaborItem laborItem) {
        return new LaborItemResponse(
                laborItem.getId(),
                laborItem.getWorkOrder().getId(),
                laborItem.getDescription(),
                laborItem.getPrice(),
                laborItem.getCreatedAt()
        );
    }
}
