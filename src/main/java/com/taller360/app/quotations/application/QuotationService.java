package com.taller360.app.quotations.application;

import com.taller360.app.quotations.application.dto.QuotationItemRequest;
import com.taller360.app.quotations.application.dto.QuotationItemResponse;
import com.taller360.app.quotations.application.dto.QuotationResponse;
import com.taller360.app.quotations.application.dto.UpdateQuotationRequest;
import com.taller360.app.quotations.domain.Quotation;
import com.taller360.app.quotations.domain.QuotationItem;
import com.taller360.app.quotations.domain.QuotationSequence;
import com.taller360.app.quotations.domain.QuotationStatus;
import com.taller360.app.quotations.infrastructure.QuotationItemRepository;
import com.taller360.app.quotations.infrastructure.QuotationRepository;
import com.taller360.app.quotations.infrastructure.QuotationSequenceRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.List;

@Service
public class QuotationService {

    private static final EnumSet<QuotationStatus> ACTIVE_STATUSES = EnumSet.of(QuotationStatus.DRAFT, QuotationStatus.SENT);

    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final QuotationSequenceRepository quotationSequenceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final QuotationProperties quotationProperties;

    public QuotationService(
            QuotationRepository quotationRepository,
            QuotationItemRepository quotationItemRepository,
            QuotationSequenceRepository quotationSequenceRepository,
            WorkOrderRepository workOrderRepository,
            QuotationProperties quotationProperties
    ) {
        this.quotationRepository = quotationRepository;
        this.quotationItemRepository = quotationItemRepository;
        this.quotationSequenceRepository = quotationSequenceRepository;
        this.workOrderRepository = workOrderRepository;
        this.quotationProperties = quotationProperties;
    }

    @Transactional
    public QuotationResponse create(Long workOrderId) {
        WorkOrder workOrder = getWorkOrder(workOrderId);
        validateWorkOrderAllowsQuotationCreation(workOrder);

        if (quotationRepository.existsByWorkOrderIdAndStatusIn(workOrderId, ACTIVE_STATUSES)) {
            throw new BusinessRuleException("A work order can have only one active quotation");
        }

        Quotation quotation = new Quotation();
        quotation.setCode(generateCode());
        quotation.setWorkOrder(workOrder);
        quotation.setStatus(QuotationStatus.DRAFT);
        quotation.recalculateTotals(taxRate());

        return toResponse(quotationRepository.save(quotation));
    }

    @Transactional(readOnly = true)
    public QuotationResponse findById(Long id) {
        return toResponse(getQuotation(id));
    }

    @Transactional
    public QuotationResponse update(Long id, UpdateQuotationRequest request) {
        Quotation quotation = getQuotation(id);
        quotation.ensureDraftEditable();
        quotation.getItems().clear();

        request.items().forEach(itemRequest -> quotation.addItem(buildItem(itemRequest)));
        quotation.recalculateTotals(taxRate());

        return toResponse(quotation);
    }

    @Transactional
    public QuotationResponse addItem(Long quotationId, QuotationItemRequest request) {
        Quotation quotation = getQuotation(quotationId);
        quotation.addItem(buildItem(request));
        quotation.recalculateTotals(taxRate());
        return toResponse(quotation);
    }

    @Transactional
    public QuotationResponse updateItem(Long quotationId, Long itemId, QuotationItemRequest request) {
        Quotation quotation = getQuotation(quotationId);
        quotation.ensureDraftEditable();

        QuotationItem item = getQuotationItem(quotationId, itemId);
        applyItemChanges(item, request);
        quotation.recalculateTotals(taxRate());

        return toResponse(quotation);
    }

    @Transactional
    public void deleteItem(Long quotationId, Long itemId) {
        Quotation quotation = getQuotation(quotationId);
        quotation.ensureDraftEditable();

        QuotationItem item = getQuotationItem(quotationId, itemId);
        quotation.removeItem(item);
        quotation.recalculateTotals(taxRate());
    }

    @Transactional
    public QuotationResponse send(Long id) {
        Quotation quotation = getQuotation(id);
        quotation.send(taxRate());
        quotation.getWorkOrder().markQuotedFromQuotationSent();
        return toResponse(quotation);
    }

    @Transactional(readOnly = true)
    public QuotationResponse findByPublicToken(String token) {
        return toResponse(getQuotationByToken(token));
    }

    @Transactional
    public QuotationResponse approveByPublicToken(String token) {
        Quotation quotation = getQuotationByToken(token);
        quotation.approve();
        quotation.getWorkOrder().markApprovedFromQuotation();
        return toResponse(quotation);
    }

    @Transactional
    public QuotationResponse rejectByPublicToken(String token) {
        Quotation quotation = getQuotationByToken(token);
        quotation.reject();
        quotation.getWorkOrder().markRejectedFromQuotation();
        return toResponse(quotation);
    }

    private String generateCode() {
        QuotationSequence sequence = quotationSequenceRepository.save(new QuotationSequence());
        return "COT-%06d".formatted(sequence.getId());
    }

    private WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
    }

    private Quotation getQuotation(Long id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));
    }

    private Quotation getQuotationByToken(String token) {
        return quotationRepository.findByPublicToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));
    }

    private QuotationItem getQuotationItem(Long quotationId, Long itemId) {
        return quotationItemRepository.findByIdAndQuotationId(itemId, quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation item not found"));
    }

    private void validateWorkOrderAllowsQuotationCreation(WorkOrder workOrder) {
        if (workOrder.getStatus() == WorkOrderStatus.DELIVERED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new BusinessRuleException("A quotation cannot be created for a DELIVERED or CANCELLED work order");
        }
    }

    private QuotationItem buildItem(QuotationItemRequest request) {
        QuotationItem item = new QuotationItem();
        applyItemChanges(item, request);
        return item;
    }

    private void applyItemChanges(QuotationItem item, QuotationItemRequest request) {
        item.setType(request.type());
        item.setDescription(request.description().trim());
        item.setQuantity(request.quantity().setScale(2, RoundingMode.HALF_UP));
        item.setUnitPrice(request.unitPrice().setScale(2, RoundingMode.HALF_UP));
        item.recalculateTotal();
    }

    private BigDecimal taxRate() {
        return quotationProperties.taxRate();
    }

    private QuotationResponse toResponse(Quotation quotation) {
        return new QuotationResponse(
                quotation.getId(),
                quotation.getCode(),
                quotation.getWorkOrder().getId(),
                quotation.getWorkOrder().getCode(),
                quotation.getWorkOrder().getVehicle().getPlate(),
                quotation.getWorkOrder().getCustomer().getFullName(),
                quotation.getStatus(),
                quotation.getSubtotal(),
                quotation.getTax(),
                quotation.getTotal(),
                quotation.getPublicToken(),
                quotation.getSentAt(),
                quotation.getCustomerDecisionAt(),
                quotation.getCreatedAt(),
                quotation.getUpdatedAt(),
                quotation.getItems().stream()
                        .map(item -> new QuotationItemResponse(
                                item.getId(),
                                item.getType(),
                                item.getDescription(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getTotal()
                        ))
                        .toList()
        );
    }
}
