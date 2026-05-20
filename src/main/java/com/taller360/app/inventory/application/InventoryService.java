package com.taller360.app.inventory.application;

import com.taller360.app.inventory.application.dto.CreateInventoryItemRequest;
import com.taller360.app.inventory.application.dto.InventoryItemResponse;
import com.taller360.app.inventory.application.dto.UpdateInventoryItemRequest;
import com.taller360.app.inventory.domain.InventoryItem;
import com.taller360.app.inventory.infrastructure.InventoryItemRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findAll(String search) {
        List<InventoryItem> items = hasText(search)
                ? inventoryItemRepository.search(search.trim())
                : inventoryItemRepository.findAll().stream()
                .sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                .toList();

        return items.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findById(Long id) {
        return toResponse(getInventoryItemEntity(id));
    }

    @Transactional
    public InventoryItemResponse create(CreateInventoryItemRequest request) {
        String normalizedSku = normalizeSku(request.sku());
        validateSkuAvailability(normalizedSku, null);

        InventoryItem item = new InventoryItem();
        applyChanges(
                item,
                request.name(),
                normalizedSku,
                request.description(),
                request.currentStock(),
                request.minStock(),
                request.unitCost(),
                request.salePrice(),
                request.active() == null || request.active()
        );

        return toResponse(inventoryItemRepository.save(item));
    }

    @Transactional
    public InventoryItemResponse update(Long id, UpdateInventoryItemRequest request) {
        InventoryItem item = getInventoryItemEntity(id);
        String normalizedSku = normalizeSku(request.sku());
        validateSkuAvailability(normalizedSku, id);

        applyChanges(
                item,
                request.name(),
                normalizedSku,
                request.description(),
                request.currentStock(),
                request.minStock(),
                request.unitCost(),
                request.salePrice(),
                request.active()
        );

        return toResponse(item);
    }

    @Transactional
    public InventoryItemResponse deactivate(Long id) {
        InventoryItem item = getInventoryItemEntity(id);
        item.deactivate();
        return toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findLowStockItems() {
        return inventoryItemRepository.findLowStockItems().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryItem getInventoryItemEntity(Long id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
    }

    private void applyChanges(
            InventoryItem item,
            String name,
            String sku,
            String description,
            BigDecimal currentStock,
            BigDecimal minStock,
            BigDecimal unitCost,
            BigDecimal salePrice,
            boolean active
    ) {
        item.setName(name.trim());
        item.setSku(sku);
        item.setDescription(normalizeNullable(description));
        item.setCurrentStock(scale(currentStock));
        item.setMinStock(scale(minStock));
        item.setUnitCost(scale(unitCost));
        item.setSalePrice(scale(salePrice));
        item.setActive(active);
    }

    private void validateSkuAvailability(String sku, Long currentItemId) {
        inventoryItemRepository.findBySkuIgnoreCase(sku)
                .filter(existingItem -> !existingItem.getId().equals(currentItemId))
                .ifPresent(existingItem -> {
                    throw new BusinessRuleException("SKU is already registered");
                });
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase(Locale.ROOT);
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

    private InventoryItemResponse toResponse(InventoryItem item) {
        return new InventoryItemResponse(
                item.getId(),
                item.getName(),
                item.getSku(),
                item.getDescription(),
                item.getCurrentStock(),
                item.getMinStock(),
                item.getUnitCost(),
                item.getSalePrice(),
                item.isActive(),
                item.isLowStock(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
