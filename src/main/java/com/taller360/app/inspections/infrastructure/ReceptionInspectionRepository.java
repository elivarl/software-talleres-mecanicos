package com.taller360.app.inspections.infrastructure;

import com.taller360.app.inspections.domain.ReceptionInspection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReceptionInspectionRepository extends JpaRepository<ReceptionInspection, Long> {

    boolean existsByWorkOrderId(Long workOrderId);

    @EntityGraph(attributePaths = {"workOrder", "photos"})
    Optional<ReceptionInspection> findById(Long id);

    @EntityGraph(attributePaths = {"workOrder", "photos"})
    Optional<ReceptionInspection> findByWorkOrderId(Long workOrderId);

    @EntityGraph(attributePaths = {"workOrder", "photos"})
    List<ReceptionInspection> findByWorkOrderIdIn(Collection<Long> workOrderIds);
}
