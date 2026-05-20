package com.taller360.app.workorders.infrastructure;

import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @EntityGraph(attributePaths = {"customer", "vehicle", "assignedMechanic"})
    Optional<WorkOrder> findById(Long id);

    @Query("""
            select wo
            from WorkOrder wo
            join wo.vehicle v
            join wo.customer c
            where (:status is null or wo.status = :status)
              and (:plate is null or lower(v.plate) like lower(concat('%', :plate, '%')))
              and (:customerId is null or c.id = :customerId)
              and (:receptionDateFrom is null or wo.receptionDate >= :receptionDateFrom)
              and (:receptionDateTo is null or wo.receptionDate <= :receptionDateTo)
            order by wo.receptionDate desc, wo.createdAt desc
            """)
    @EntityGraph(attributePaths = {"customer", "vehicle", "assignedMechanic"})
    List<WorkOrder> search(
            @Param("status") WorkOrderStatus status,
            @Param("plate") String plate,
            @Param("customerId") Long customerId,
            @Param("receptionDateFrom") LocalDateTime receptionDateFrom,
            @Param("receptionDateTo") LocalDateTime receptionDateTo
    );

    @EntityGraph(attributePaths = {"customer", "vehicle", "assignedMechanic"})
    List<WorkOrder> findByVehicleIdOrderByReceptionDateDescCreatedAtDesc(Long vehicleId);
}
