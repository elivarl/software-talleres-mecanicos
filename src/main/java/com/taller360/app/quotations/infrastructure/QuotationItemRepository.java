package com.taller360.app.quotations.infrastructure;

import com.taller360.app.quotations.domain.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuotationItemRepository extends JpaRepository<QuotationItem, Long> {

    Optional<QuotationItem> findByIdAndQuotationId(Long id, Long quotationId);
}
