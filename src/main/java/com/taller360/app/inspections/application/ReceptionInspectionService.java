package com.taller360.app.inspections.application;

import com.taller360.app.inspections.application.dto.AddInspectionPhotoRequest;
import com.taller360.app.inspections.application.dto.CreateReceptionInspectionRequest;
import com.taller360.app.inspections.application.dto.InspectionPhotoResponse;
import com.taller360.app.inspections.application.dto.ReceptionInspectionResponse;
import com.taller360.app.inspections.application.dto.UpdateReceptionInspectionRequest;
import com.taller360.app.inspections.domain.InspectionPhoto;
import com.taller360.app.inspections.domain.ReceptionInspection;
import com.taller360.app.inspections.infrastructure.InspectionPhotoRepository;
import com.taller360.app.inspections.infrastructure.ReceptionInspectionRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceptionInspectionService {

    private final ReceptionInspectionRepository receptionInspectionRepository;
    private final InspectionPhotoRepository inspectionPhotoRepository;
    private final WorkOrderRepository workOrderRepository;

    public ReceptionInspectionService(
            ReceptionInspectionRepository receptionInspectionRepository,
            InspectionPhotoRepository inspectionPhotoRepository,
            WorkOrderRepository workOrderRepository
    ) {
        this.receptionInspectionRepository = receptionInspectionRepository;
        this.inspectionPhotoRepository = inspectionPhotoRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public ReceptionInspectionResponse create(Long workOrderId, CreateReceptionInspectionRequest request) {
        if (receptionInspectionRepository.existsByWorkOrderId(workOrderId)) {
            throw new BusinessRuleException("A work order can have only one initial inspection");
        }

        WorkOrder workOrder = getWorkOrder(workOrderId);
        ensureInspectionCanBeModified(workOrder);

        ReceptionInspection inspection = new ReceptionInspection();
        inspection.setWorkOrder(workOrder);
        applyChanges(
                inspection,
                request.mileage(),
                request.fuelLevel(),
                request.exteriorCondition(),
                request.visibleScratches(),
                request.visibleDents(),
                request.lightsWorking(),
                request.tiresCondition(),
                request.mirrorsCondition(),
                request.hasSpareTire(),
                request.hasJack(),
                request.hasTools(),
                request.hasDocuments(),
                request.personalItemsNotes(),
                request.generalNotes()
        );

        return toResponse(receptionInspectionRepository.save(inspection));
    }

    @Transactional(readOnly = true)
    public ReceptionInspectionResponse findByWorkOrderId(Long workOrderId) {
        ReceptionInspection inspection = receptionInspectionRepository.findByWorkOrderId(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found"));
        return toResponse(inspection);
    }

    @Transactional
    public ReceptionInspectionResponse update(Long id, UpdateReceptionInspectionRequest request) {
        ReceptionInspection inspection = getInspection(id);
        ensureInspectionCanBeModified(inspection.getWorkOrder());

        applyChanges(
                inspection,
                request.mileage(),
                request.fuelLevel(),
                request.exteriorCondition(),
                request.visibleScratches(),
                request.visibleDents(),
                request.lightsWorking(),
                request.tiresCondition(),
                request.mirrorsCondition(),
                request.hasSpareTire(),
                request.hasJack(),
                request.hasTools(),
                request.hasDocuments(),
                request.personalItemsNotes(),
                request.generalNotes()
        );

        return toResponse(inspection);
    }

    @Transactional
    public ReceptionInspectionResponse addPhoto(Long id, AddInspectionPhotoRequest request) {
        ReceptionInspection inspection = getInspection(id);
        ensureInspectionCanBeModified(inspection.getWorkOrder());

        InspectionPhoto photo = new InspectionPhoto();
        photo.setPhotoUrl(request.photoUrl().trim());
        photo.setDescription(normalizeNullable(request.description()));
        inspection.addPhoto(photo);
        inspectionPhotoRepository.save(photo);

        return toResponse(inspection);
    }

    private ReceptionInspection getInspection(Long id) {
        return receptionInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found"));
    }

    private WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
    }

    private void ensureInspectionCanBeModified(WorkOrder workOrder) {
        if (workOrder.getStatus() == WorkOrderStatus.DELIVERED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new BusinessRuleException("Inspection cannot be modified after the work order is DELIVERED or CANCELLED");
        }
    }

    private void applyChanges(
            ReceptionInspection inspection,
            Long mileage,
            String fuelLevel,
            String exteriorCondition,
            String visibleScratches,
            String visibleDents,
            Boolean lightsWorking,
            String tiresCondition,
            String mirrorsCondition,
            Boolean hasSpareTire,
            Boolean hasJack,
            Boolean hasTools,
            Boolean hasDocuments,
            String personalItemsNotes,
            String generalNotes
    ) {
        inspection.setMileage(mileage);
        inspection.setFuelLevel(normalizeNullable(fuelLevel));
        inspection.setExteriorCondition(normalizeNullable(exteriorCondition));
        inspection.setVisibleScratches(normalizeNullable(visibleScratches));
        inspection.setVisibleDents(normalizeNullable(visibleDents));
        inspection.setLightsWorking(lightsWorking);
        inspection.setTiresCondition(normalizeNullable(tiresCondition));
        inspection.setMirrorsCondition(normalizeNullable(mirrorsCondition));
        inspection.setHasSpareTire(hasSpareTire);
        inspection.setHasJack(hasJack);
        inspection.setHasTools(hasTools);
        inspection.setHasDocuments(hasDocuments);
        inspection.setPersonalItemsNotes(normalizeNullable(personalItemsNotes));
        inspection.setGeneralNotes(normalizeNullable(generalNotes));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private ReceptionInspectionResponse toResponse(ReceptionInspection inspection) {
        return new ReceptionInspectionResponse(
                inspection.getId(),
                inspection.getWorkOrder().getId(),
                inspection.getWorkOrder().getCode(),
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
                inspection.getPhotos().stream()
                        .map(photo -> new InspectionPhotoResponse(
                                photo.getId(),
                                photo.getPhotoUrl(),
                                photo.getDescription(),
                                photo.getCreatedAt()
                        ))
                        .toList()
        );
    }
}
