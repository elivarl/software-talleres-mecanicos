package com.taller360.app.inventory.infrastructure;

import com.taller360.app.inventory.domain.WorkOrderPart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPart, Long> {

    @EntityGraph(attributePaths = {"inventoryItem", "workOrder"})
    List<WorkOrderPart> findByWorkOrderIdOrderByCreatedAtAsc(Long workOrderId);

    @EntityGraph(attributePaths = {"inventoryItem", "workOrder"})
    List<WorkOrderPart> findByWorkOrderIdInOrderByCreatedAtAsc(Collection<Long> workOrderIds);

    @EntityGraph(attributePaths = {"inventoryItem", "workOrder"})
    Optional<WorkOrderPart> findByIdAndWorkOrderId(Long id, Long workOrderId);
}
