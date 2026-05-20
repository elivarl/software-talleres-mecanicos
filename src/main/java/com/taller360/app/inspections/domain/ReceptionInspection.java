package com.taller360.app.inspections.domain;

import com.taller360.app.workorders.domain.WorkOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reception_inspections")
public class ReceptionInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false, unique = true)
    private WorkOrder workOrder;

    @Column(name = "mileage", nullable = false)
    private Long mileage;

    @Column(name = "fuel_level", length = 50)
    private String fuelLevel;

    @Column(name = "exterior_condition", length = 255)
    private String exteriorCondition;

    @Column(name = "visible_scratches", columnDefinition = "TEXT")
    private String visibleScratches;

    @Column(name = "visible_dents", columnDefinition = "TEXT")
    private String visibleDents;

    @Column(name = "lights_working")
    private Boolean lightsWorking;

    @Column(name = "tires_condition", length = 255)
    private String tiresCondition;

    @Column(name = "mirrors_condition", length = 255)
    private String mirrorsCondition;

    @Column(name = "has_spare_tire")
    private Boolean hasSpareTire;

    @Column(name = "has_jack")
    private Boolean hasJack;

    @Column(name = "has_tools")
    private Boolean hasTools;

    @Column(name = "has_documents")
    private Boolean hasDocuments;

    @Column(name = "personal_items_notes", columnDefinition = "TEXT")
    private String personalItemsNotes;

    @Column(name = "general_notes", columnDefinition = "TEXT")
    private String generalNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt asc")
    private List<InspectionPhoto> photos = new ArrayList<>();

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

    public void addPhoto(InspectionPhoto photo) {
        photo.setInspection(this);
        this.photos.add(photo);
    }

    public Long getId() {
        return id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getExteriorCondition() {
        return exteriorCondition;
    }

    public void setExteriorCondition(String exteriorCondition) {
        this.exteriorCondition = exteriorCondition;
    }

    public String getVisibleScratches() {
        return visibleScratches;
    }

    public void setVisibleScratches(String visibleScratches) {
        this.visibleScratches = visibleScratches;
    }

    public String getVisibleDents() {
        return visibleDents;
    }

    public void setVisibleDents(String visibleDents) {
        this.visibleDents = visibleDents;
    }

    public Boolean getLightsWorking() {
        return lightsWorking;
    }

    public void setLightsWorking(Boolean lightsWorking) {
        this.lightsWorking = lightsWorking;
    }

    public String getTiresCondition() {
        return tiresCondition;
    }

    public void setTiresCondition(String tiresCondition) {
        this.tiresCondition = tiresCondition;
    }

    public String getMirrorsCondition() {
        return mirrorsCondition;
    }

    public void setMirrorsCondition(String mirrorsCondition) {
        this.mirrorsCondition = mirrorsCondition;
    }

    public Boolean getHasSpareTire() {
        return hasSpareTire;
    }

    public void setHasSpareTire(Boolean hasSpareTire) {
        this.hasSpareTire = hasSpareTire;
    }

    public Boolean getHasJack() {
        return hasJack;
    }

    public void setHasJack(Boolean hasJack) {
        this.hasJack = hasJack;
    }

    public Boolean getHasTools() {
        return hasTools;
    }

    public void setHasTools(Boolean hasTools) {
        this.hasTools = hasTools;
    }

    public Boolean getHasDocuments() {
        return hasDocuments;
    }

    public void setHasDocuments(Boolean hasDocuments) {
        this.hasDocuments = hasDocuments;
    }

    public String getPersonalItemsNotes() {
        return personalItemsNotes;
    }

    public void setPersonalItemsNotes(String personalItemsNotes) {
        this.personalItemsNotes = personalItemsNotes;
    }

    public String getGeneralNotes() {
        return generalNotes;
    }

    public void setGeneralNotes(String generalNotes) {
        this.generalNotes = generalNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<InspectionPhoto> getPhotos() {
        return photos;
    }
}
