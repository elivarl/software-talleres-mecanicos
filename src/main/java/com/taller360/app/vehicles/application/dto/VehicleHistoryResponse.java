package com.taller360.app.vehicles.application.dto;

import com.taller360.app.customers.application.dto.CustomerResponse;

import java.util.List;

public record VehicleHistoryResponse(
        VehicleResponse vehicle,
        CustomerResponse customer,
        List<VehicleHistoryWorkOrderResponse> workOrders
) {
}
