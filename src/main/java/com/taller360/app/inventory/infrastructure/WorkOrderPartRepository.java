package com.taller360.app.inventory.infrastructure;

import com.taller360.app.inventory.domain.WorkOrderPart;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Query("""
            select coalesce(sum(part.total), 0)
            from WorkOrderPart part
            join part.workOrder workOrder
            where workOrder.receptionDate >= :from
              and workOrder.receptionDate < :to
              and workOrder.status not in :excludedStatuses
            """)
    BigDecimal sumTotalForWorkOrdersReceivedBetweenExcludingStatuses(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("excludedStatuses") Collection<WorkOrderStatus> excludedStatuses
    );
}
