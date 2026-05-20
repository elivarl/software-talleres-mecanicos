package com.taller360.app.vehicles.infrastructure;

import com.taller360.app.vehicles.domain.Vehicle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @EntityGraph(attributePaths = "customer")
    Optional<Vehicle> findWithCustomerById(Long id);

    @EntityGraph(attributePaths = "customer")
    Optional<Vehicle> findWithCustomerByPlateIgnoreCase(String plate);

    Optional<Vehicle> findByPlateIgnoreCase(String plate);

    boolean existsByPlateIgnoreCase(String plate);

    List<Vehicle> findByPlateContainingIgnoreCaseOrderByPlateAsc(String plate);

    @Override
    @EntityGraph(attributePaths = "customer")
    List<Vehicle> findAll();
}
