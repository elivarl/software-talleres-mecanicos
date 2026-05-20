package com.taller360.app.inventory.domain;

import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.InsufficientStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "sku", nullable = false, unique = true, length = 80)
    private String sku;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "current_stock", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentStock;

    @Column(name = "min_stock", nullable = false, precision = 19, scale = 2)
    private BigDecimal minStock;

    @Column(name = "unit_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "sale_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
    }

    public void ensureActiveForNewWorkOrderUse() {
        if (!this.active) {
            throw new BusinessRuleException("An inactive inventory item cannot be used in new work orders");
        }
    }

    public void ensureSufficientStock(BigDecimal quantity) {
        if (this.currentStock.compareTo(quantity) < 0) {
            throw new InsufficientStockException("Stock cannot be reduced if there is not enough quantity");
        }
    }

    public void reduceStock(BigDecimal quantity) {
        ensureSufficientStock(quantity);
        this.currentStock = this.currentStock.subtract(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    public void restoreStock(BigDecimal quantity) {
        this.currentStock = this.currentStock.add(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isLowStock() {
        return this.currentStock.compareTo(this.minStock) <= 0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
