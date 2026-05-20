package com.taller360.app.quotations.infrastructure;

import com.taller360.app.quotations.domain.Quotation;
import com.taller360.app.quotations.domain.QuotationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    @Override
    @EntityGraph(attributePaths = {"workOrder", "workOrder.customer", "workOrder.vehicle", "items"})
    Optional<Quotation> findById(Long id);

    @EntityGraph(attributePaths = {"workOrder", "workOrder.customer", "workOrder.vehicle", "items"})
    Optional<Quotation> findByPublicToken(String publicToken);

    @EntityGraph(attributePaths = {"workOrder", "workOrder.customer", "workOrder.vehicle", "items"})
    List<Quotation> findByWorkOrderIdInOrderByCreatedAtDesc(Collection<Long> workOrderIds);

    boolean existsByWorkOrderIdAndStatusIn(Long workOrderId, Collection<QuotationStatus> statuses);
}
