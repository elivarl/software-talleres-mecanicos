package com.taller360.app.inspections.application;

import com.taller360.app.inspections.application.dto.UpdateReceptionInspectionRequest;
import com.taller360.app.inspections.domain.ReceptionInspection;
import com.taller360.app.inspections.infrastructure.InspectionPhotoRepository;
import com.taller360.app.inspections.infrastructure.ReceptionInspectionRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceptionInspectionServiceTest {

    @Mock
    private ReceptionInspectionRepository receptionInspectionRepository;

    @Mock
    private InspectionPhotoRepository inspectionPhotoRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private ReceptionInspectionService receptionInspectionService;

    @Test
    void shouldNotUpdateInspectionWhenWorkOrderIsDelivered() {
        ReceptionInspection inspection = new ReceptionInspection();
        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(WorkOrderStatus.DELIVERED);
        inspection.setWorkOrder(workOrder);

        when(receptionInspectionRepository.findById(1L)).thenReturn(Optional.of(inspection));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> receptionInspectionService.update(1L, request())
        );

        assertEquals("Inspection cannot be modified after the work order is DELIVERED or CANCELLED", exception.getMessage());
    }

    @Test
    void shouldNotUpdateInspectionWhenWorkOrderIsCancelled() {
        ReceptionInspection inspection = new ReceptionInspection();
        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(WorkOrderStatus.CANCELLED);
        inspection.setWorkOrder(workOrder);

        when(receptionInspectionRepository.findById(2L)).thenReturn(Optional.of(inspection));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> receptionInspectionService.update(2L, request())
        );

        assertEquals("Inspection cannot be modified after the work order is DELIVERED or CANCELLED", exception.getMessage());
    }

    private UpdateReceptionInspectionRequest request() {
        return new UpdateReceptionInspectionRequest(
                120000L,
                "Half",
                "Good",
                null,
                null,
                true,
                "Good",
                "Good",
                true,
                true,
                true,
                true,
                null,
                null
        );
    }
}
