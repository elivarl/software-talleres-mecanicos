package com.taller360.app.workorders.domain;

import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkOrderTest {

    @Test
    void shouldNotMoveToInProgressIfNotApproved() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.DIAGNOSIS);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> workOrder.updateStatus(WorkOrderStatus.IN_PROGRESS)
        );

        assertEquals("Cannot move to IN_PROGRESS if work order is not APPROVED", exception.getMessage());
    }

    @Test
    void shouldNotMoveToDeliveredIfNotReady() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.DIAGNOSIS);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> workOrder.updateStatus(WorkOrderStatus.DELIVERED)
        );

        assertEquals("Cannot move to DELIVERED if work order is not READY", exception.getMessage());
    }

    @Test
    void shouldNotMoveToReadyUnlessQualityControlIsCompleted() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.IN_PROGRESS);
        workOrder.setQualityControlCompleted(false);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> workOrder.updateStatus(WorkOrderStatus.READY)
        );

        assertEquals("Cannot move to READY unless quality control is completed", exception.getMessage());
    }

    @Test
    void shouldNotMoveToApprovedWithoutApprovedQuotation() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.QUOTED);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> workOrder.updateStatus(WorkOrderStatus.APPROVED)
        );

        assertEquals("Cannot move to APPROVED without an approved quotation", exception.getMessage());
    }

    @Test
    void shouldNotModifyDeliveredWorkOrder() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.DELIVERED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> workOrder.updateInternalNotes("Administrative note")
        );

        assertEquals("A delivered work order cannot be modified", exception.getMessage());
    }

    @Test
    void shouldNotMoveCancelledWorkOrderToAnotherStatus() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.CANCELLED);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> workOrder.updateStatus(WorkOrderStatus.DIAGNOSIS)
        );

        assertEquals("A cancelled work order cannot move to another status", exception.getMessage());
    }

    @Test
    void shouldMoveReceivedToDiagnosisWhenDiagnosisIsRegistered() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.RECEIVED);

        workOrder.registerDiagnosis("Brake pads need replacement");

        assertEquals(WorkOrderStatus.DIAGNOSIS, workOrder.getStatus());
        assertEquals("Brake pads need replacement", workOrder.getDiagnosis());
    }

    private WorkOrder workOrderWithStatus(WorkOrderStatus status) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(status);
        return workOrder;
    }
}
