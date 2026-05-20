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

    @Test
    void shouldNotAllowLaborRegistrationIfWorkOrderIsNotApprovedOrInProgress() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.READY);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                workOrder::ensureAllowsLaborRegistration
        );

        assertEquals("Labor can only be added when the work order is APPROVED or IN_PROGRESS", exception.getMessage());
    }

    @Test
    void shouldNotAllowLaborDeletionIfWorkOrderIsDelivered() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.DELIVERED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                workOrder::ensureAllowsLaborDeletion
        );

        assertEquals("Labor cannot be deleted from a DELIVERED work order", exception.getMessage());
    }

    @Test
    void shouldNotCompleteQualityControlIfWorkOrderIsNotInProgress() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.APPROVED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> workOrder.completeQualityControl("Road test completed")
        );

        assertEquals("Quality control can only be completed if work order is IN_PROGRESS", exception.getMessage());
    }

    @Test
    void shouldNotModifyQualityControlAfterDelivered() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.DELIVERED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> workOrder.completeQualityControl("Road test completed")
        );

        assertEquals("Quality control cannot be modified after DELIVERED", exception.getMessage());
    }

    @Test
    void shouldMarkWorkOrderReadyWhenQualityControlIsCompleted() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.IN_PROGRESS);
        workOrder.completeQualityControl("Final inspection passed");

        workOrder.markReady();

        assertEquals(WorkOrderStatus.READY, workOrder.getStatus());
        assertEquals("Final inspection passed", workOrder.getQualityControlNotes());
    }

    @Test
    void shouldNotMarkRejectedWorkOrderReady() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.REJECTED);
        workOrder.setQualityControlCompleted(true);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                workOrder::markReady
        );

        assertEquals("Cannot move to READY if work order is not IN_PROGRESS", exception.getMessage());
    }

    @Test
    void shouldNotMarkCancelledWorkOrderReady() {
        WorkOrder workOrder = workOrderWithStatus(WorkOrderStatus.CANCELLED);
        workOrder.setQualityControlCompleted(true);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                workOrder::markReady
        );

        assertEquals("A cancelled work order cannot move to another status", exception.getMessage());
    }

    private WorkOrder workOrderWithStatus(WorkOrderStatus status) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(status);
        return workOrder;
    }
}
