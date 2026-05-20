package com.taller360.app.inventory.infrastructure;

import com.taller360.app.inventory.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findBySkuIgnoreCase(String sku);

    @Query("""
            select i
            from InventoryItem i
            where lower(i.name) like lower(concat('%', :search, '%'))
               or lower(i.sku) like lower(concat('%', :search, '%'))
            order by i.name asc
            """)
    List<InventoryItem> search(@Param("search") String search);

    @Query("""
            select i
            from InventoryItem i
            where i.currentStock <= i.minStock
            order by i.currentStock asc, i.name asc
            """)
    List<InventoryItem> findLowStockItems();
}
