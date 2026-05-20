package com.taller360.app.quotations.infrastructure;

import com.taller360.app.quotations.domain.QuotationSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationSequenceRepository extends JpaRepository<QuotationSequence, Long> {
}
