package com.taller360.app.dashboard.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalWorkOrdersThisMonth,
        Map<String, Long> workOrdersByStatus,
        BigDecimal estimatedRevenueThisMonth,
        long pendingWorkOrders,
        long readyToDeliverWorkOrders,
        List<DashboardLowStockItemResponse> lowStockItems,
        long deliveredWorkOrdersThisMonth
) {
}
