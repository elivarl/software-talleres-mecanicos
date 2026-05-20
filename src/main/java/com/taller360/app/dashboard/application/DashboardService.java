package com.taller360.app.dashboard.application;

import com.taller360.app.dashboard.application.dto.DashboardLowStockItemResponse;
import com.taller360.app.dashboard.application.dto.DashboardResponse;
import com.taller360.app.inventory.domain.InventoryItem;
import com.taller360.app.inventory.infrastructure.InventoryItemRepository;
import com.taller360.app.inventory.infrastructure.WorkOrderPartRepository;
import com.taller360.app.labor.infrastructure.LaborItemRepository;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final EnumSet<WorkOrderStatus> NON_PENDING_STATUSES =
            EnumSet.of(WorkOrderStatus.DELIVERED, WorkOrderStatus.CANCELLED, WorkOrderStatus.REJECTED);

    private static final EnumSet<WorkOrderStatus> REVENUE_EXCLUDED_STATUSES =
            EnumSet.of(WorkOrderStatus.CANCELLED, WorkOrderStatus.REJECTED);

    private final WorkOrderRepository workOrderRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final WorkOrderPartRepository workOrderPartRepository;
    private final LaborItemRepository laborItemRepository;

    public DashboardService(
            WorkOrderRepository workOrderRepository,
            InventoryItemRepository inventoryItemRepository,
            WorkOrderPartRepository workOrderPartRepository,
            LaborItemRepository laborItemRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.workOrderPartRepository = workOrderPartRepository;
        this.laborItemRepository = laborItemRepository;
    }

    public DashboardResponse getDashboard() {
        LocalDateTime monthStart = YearMonth.from(LocalDate.now()).atDay(1).atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);

        long totalWorkOrdersThisMonth =
                workOrderRepository.countByReceptionDateGreaterThanEqualAndReceptionDateLessThan(monthStart, nextMonthStart);
        long pendingWorkOrders = workOrderRepository.countByStatusNotIn(NON_PENDING_STATUSES);
        long readyToDeliverWorkOrders = workOrderRepository.countByStatus(WorkOrderStatus.READY);
        long deliveredWorkOrdersThisMonth = workOrderRepository.countDeliveredBetween(monthStart, nextMonthStart);

        BigDecimal usedPartsRevenue = workOrderPartRepository
                .sumTotalForWorkOrdersReceivedBetweenExcludingStatuses(monthStart, nextMonthStart, REVENUE_EXCLUDED_STATUSES);
        BigDecimal laborRevenue = laborItemRepository
                .sumPriceForWorkOrdersReceivedBetweenExcludingStatuses(monthStart, nextMonthStart, REVENUE_EXCLUDED_STATUSES);

        return new DashboardResponse(
                totalWorkOrdersThisMonth,
                buildStatusMap(),
                usedPartsRevenue.add(laborRevenue),
                pendingWorkOrders,
                readyToDeliverWorkOrders,
                inventoryItemRepository.findLowStockItems().stream().map(this::toLowStockResponse).toList(),
                deliveredWorkOrdersThisMonth
        );
    }

    private Map<String, Long> buildStatusMap() {
        Map<String, Long> counts = new LinkedHashMap<>();
        Arrays.stream(WorkOrderStatus.values()).forEach(status -> counts.put(status.name(), 0L));
        workOrderRepository.countGroupedByStatus()
                .forEach(count -> counts.put(count.getStatus().name(), count.getTotal()));
        return counts;
    }

    private DashboardLowStockItemResponse toLowStockResponse(InventoryItem inventoryItem) {
        return new DashboardLowStockItemResponse(
                inventoryItem.getId(),
                inventoryItem.getName(),
                inventoryItem.getSku(),
                inventoryItem.getCurrentStock(),
                inventoryItem.getMinStock(),
                inventoryItem.isActive()
        );
    }
}
