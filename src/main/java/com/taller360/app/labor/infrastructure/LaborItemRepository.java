package com.taller360.app.labor.infrastructure;

import com.taller360.app.labor.domain.LaborItem;
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

public interface LaborItemRepository extends JpaRepository<LaborItem, Long> {

    @EntityGraph(attributePaths = "workOrder")
    List<LaborItem> findByWorkOrderIdOrderByCreatedAtAsc(Long workOrderId);

    @EntityGraph(attributePaths = "workOrder")
    List<LaborItem> findByWorkOrderIdInOrderByCreatedAtAsc(Collection<Long> workOrderIds);

    @EntityGraph(attributePaths = "workOrder")
    Optional<LaborItem> findByIdAndWorkOrderId(Long id, Long workOrderId);

    @Query("""
            select coalesce(sum(labor.price), 0)
            from LaborItem labor
            join labor.workOrder workOrder
            where workOrder.receptionDate >= :from
              and workOrder.receptionDate < :to
              and workOrder.status not in :excludedStatuses
            """)
    BigDecimal sumPriceForWorkOrdersReceivedBetweenExcludingStatuses(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("excludedStatuses") Collection<WorkOrderStatus> excludedStatuses
    );
}
