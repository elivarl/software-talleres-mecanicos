package com.taller360.app.inspections.infrastructure;

import com.taller360.app.inspections.domain.InspectionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionPhotoRepository extends JpaRepository<InspectionPhoto, Long> {
}
