package com.taller360.app.workorders.domain;

import com.taller360.app.customers.domain.Customer;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.InvalidStatusTransitionException;
import com.taller360.app.users.domain.User;
import com.taller360.app.vehicles.domain.Vehicle;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_mechanic_id")
    private User assignedMechanic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WorkOrderStatus status;

    @Column(name = "reception_date", nullable = false)
    private LocalDateTime receptionDate;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "current_mileage", nullable = false)
    private Long currentMileage;

    @Column(name = "fuel_level", length = 50)
    private String fuelLevel;

    @Column(name = "customer_complaint", nullable = false, columnDefinition = "TEXT")
    private String customerComplaint;

    @Column(name = "initial_observations", columnDefinition = "TEXT")
    private String initialObservations;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "quality_control_completed", nullable = false)
    private boolean qualityControlCompleted;

    @Column(name = "quality_control_notes", columnDefinition = "TEXT")
    private String qualityControlNotes;

    @Column(name = "ready_at")
    private LocalDateTime readyAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "delivered_to", length = 150)
    private String deliveredTo;

    @Column(name = "final_mileage")
    private Long finalMileage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = WorkOrderStatus.RECEIVED;
        }
        if (this.receptionDate == null) {
            this.receptionDate = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void assignMechanic(User mechanic) {
        ensureImportantFieldsAreModifiable();
        this.assignedMechanic = mechanic;
    }

    public void updateStatus(WorkOrderStatus targetStatus) {
        if (this.status == WorkOrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("A cancelled work order cannot move to another status");
        }

        if (this.status == WorkOrderStatus.DELIVERED) {
            throw new InvalidStatusTransitionException("A delivered work order cannot change status");
        }

        if (targetStatus == this.status) {
            return;
        }

        switch (targetStatus) {
            case RECEIVED -> throw new InvalidStatusTransitionException("A work order cannot move back to RECEIVED");
            case DIAGNOSIS -> moveToDiagnosis();
            case QUOTED -> throw new InvalidStatusTransitionException("Cannot move to QUOTED without sending a quotation");
            case APPROVED -> throw new InvalidStatusTransitionException("Cannot move to APPROVED without an approved quotation");
            case REJECTED -> throw new InvalidStatusTransitionException("Cannot move to REJECTED without a rejected quotation");
            case IN_PROGRESS -> {
                if (this.status != WorkOrderStatus.APPROVED) {
                    throw new InvalidStatusTransitionException("Cannot move to IN_PROGRESS if work order is not APPROVED");
                }
                this.status = WorkOrderStatus.IN_PROGRESS;
            }
            case READY -> {
                if (!this.qualityControlCompleted) {
                    throw new InvalidStatusTransitionException("Cannot move to READY unless quality control is completed");
                }
                if (this.status != WorkOrderStatus.IN_PROGRESS) {
                    throw new InvalidStatusTransitionException("Cannot move to READY if work order is not IN_PROGRESS");
                }
                this.status = WorkOrderStatus.READY;
            }
            case DELIVERED -> {
                if (this.status != WorkOrderStatus.READY) {
                    throw new InvalidStatusTransitionException("Cannot move to DELIVERED if work order is not READY");
                }
                this.status = WorkOrderStatus.DELIVERED;
            }
            case CANCELLED -> {
                if (this.status != WorkOrderStatus.RECEIVED
                        && this.status != WorkOrderStatus.DIAGNOSIS
                        && this.status != WorkOrderStatus.QUOTED) {
                    throw new InvalidStatusTransitionException("Only RECEIVED, DIAGNOSIS or QUOTED work orders can be cancelled");
                }
                this.status = WorkOrderStatus.CANCELLED;
            }
        }
    }

    public void registerDiagnosis(String diagnosis) {
        ensureImportantFieldsAreModifiable();
        this.diagnosis = diagnosis;
        if (this.status == WorkOrderStatus.RECEIVED) {
            this.status = WorkOrderStatus.DIAGNOSIS;
        }
    }

    public void updateInternalNotes(String internalNotes) {
        ensureImportantFieldsAreModifiable();
        this.internalNotes = internalNotes;
    }

    public void ensureImportantFieldsAreModifiable() {
        if (this.status == WorkOrderStatus.DELIVERED) {
            throw new BusinessRuleException("A delivered work order cannot be modified");
        }
        if (this.status == WorkOrderStatus.CANCELLED) {
            throw new BusinessRuleException("A cancelled work order cannot be modified");
        }
    }

    private void moveToDiagnosis() {
        if (this.status != WorkOrderStatus.RECEIVED) {
            throw new InvalidStatusTransitionException("A work order can only move to DIAGNOSIS from RECEIVED");
        }
        this.status = WorkOrderStatus.DIAGNOSIS;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public User getAssignedMechanic() {
        return assignedMechanic;
    }

    public void setAssignedMechanic(User assignedMechanic) {
        this.assignedMechanic = assignedMechanic;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getReceptionDate() {
        return receptionDate;
    }

    public void setReceptionDate(LocalDateTime receptionDate) {
        this.receptionDate = receptionDate;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Long getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Long currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getCustomerComplaint() {
        return customerComplaint;
    }

    public void setCustomerComplaint(String customerComplaint) {
        this.customerComplaint = customerComplaint;
    }

    public String getInitialObservations() {
        return initialObservations;
    }

    public void setInitialObservations(String initialObservations) {
        this.initialObservations = initialObservations;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public boolean isQualityControlCompleted() {
        return qualityControlCompleted;
    }

    public void setQualityControlCompleted(boolean qualityControlCompleted) {
        this.qualityControlCompleted = qualityControlCompleted;
    }

    public String getQualityControlNotes() {
        return qualityControlNotes;
    }

    public void setQualityControlNotes(String qualityControlNotes) {
        this.qualityControlNotes = qualityControlNotes;
    }

    public LocalDateTime getReadyAt() {
        return readyAt;
    }

    public void setReadyAt(LocalDateTime readyAt) {
        this.readyAt = readyAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getDeliveredTo() {
        return deliveredTo;
    }

    public void setDeliveredTo(String deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    public Long getFinalMileage() {
        return finalMileage;
    }

    public void setFinalMileage(Long finalMileage) {
        this.finalMileage = finalMileage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
