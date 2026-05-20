package com.taller360.app.workorders.infrastructure;

import com.taller360.app.workorders.domain.WorkOrderSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderSequenceRepository extends JpaRepository<WorkOrderSequence, Long> {
}
