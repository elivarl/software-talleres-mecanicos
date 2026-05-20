package com.taller360.app.labor.infrastructure;

import com.taller360.app.labor.domain.LaborItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
