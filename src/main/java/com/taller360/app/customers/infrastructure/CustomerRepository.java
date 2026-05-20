package com.taller360.app.customers.infrastructure;

import com.taller360.app.customers.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdentification(String identification);

    boolean existsByIdentification(String identification);

    @Query("""
            select c
            from Customer c
            where lower(c.fullName) like lower(concat('%', :search, '%'))
               or lower(coalesce(c.identification, '')) like lower(concat('%', :search, '%'))
               or lower(c.phone) like lower(concat('%', :search, '%'))
            order by c.fullName asc
            """)
    List<Customer> search(@Param("search") String search);
}
