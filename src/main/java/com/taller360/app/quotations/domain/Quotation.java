package com.taller360.app.quotations.domain;

import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.QuotationAlreadyDecidedException;
import com.taller360.app.workorders.domain.WorkOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quotations")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuotationStatus status;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax", nullable = false, precision = 19, scale = 2)
    private BigDecimal tax;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "public_token", unique = true, length = 100)
    private String publicToken;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "customer_decision_at")
    private LocalDateTime customerDecisionAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<QuotationItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = QuotationStatus.DRAFT;
        }
        if (this.subtotal == null) {
            this.subtotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (this.tax == null) {
            this.tax = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (this.total == null) {
            this.total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void ensureDraftEditable() {
        if (this.status != QuotationStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT quotations can be modified");
        }
    }

    public void addItem(QuotationItem item) {
        ensureDraftEditable();
        item.setQuotation(this);
        this.items.add(item);
    }

    public void removeItem(QuotationItem item) {
        ensureDraftEditable();
        this.items.remove(item);
        item.setQuotation(null);
    }

    public void recalculateTotals(BigDecimal taxRate) {
        BigDecimal calculatedSubtotal = this.items.stream()
                .map(QuotationItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        this.subtotal = calculatedSubtotal;
        this.tax = calculatedSubtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        this.total = this.subtotal.add(this.tax).setScale(2, RoundingMode.HALF_UP);
    }

    public void send(BigDecimal taxRate) {
        ensureDraftEditable();
        if (this.items.isEmpty()) {
            throw new BusinessRuleException("A quotation without items cannot be sent");
        }
        recalculateTotals(taxRate);
        if (this.publicToken == null) {
            this.publicToken = UUID.randomUUID().toString();
        }
        this.status = QuotationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void approve() {
        if (this.status == QuotationStatus.APPROVED) {
            throw new QuotationAlreadyDecidedException("A quotation cannot be approved twice");
        }
        if (this.status == QuotationStatus.REJECTED) {
            throw new QuotationAlreadyDecidedException("A quotation cannot be approved after being rejected");
        }
        if (this.status != QuotationStatus.SENT) {
            throw new BusinessRuleException("Only SENT quotations can be approved");
        }
        this.status = QuotationStatus.APPROVED;
        this.customerDecisionAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status == QuotationStatus.REJECTED) {
            throw new QuotationAlreadyDecidedException("A quotation cannot be rejected twice");
        }
        if (this.status == QuotationStatus.APPROVED) {
            throw new QuotationAlreadyDecidedException("A quotation cannot be rejected after being approved");
        }
        if (this.status != QuotationStatus.SENT) {
            throw new BusinessRuleException("Only SENT quotations can be rejected");
        }
        this.status = QuotationStatus.REJECTED;
        this.customerDecisionAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public QuotationStatus getStatus() {
        return status;
    }

    public void setStatus(QuotationStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getCustomerDecisionAt() {
        return customerDecisionAt;
    }

    public void setCustomerDecisionAt(LocalDateTime customerDecisionAt) {
        this.customerDecisionAt = customerDecisionAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<QuotationItem> getItems() {
        return items;
    }
}
