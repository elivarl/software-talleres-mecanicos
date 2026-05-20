package com.taller360.app.dashboard.web;

import com.taller360.app.Taller360Application;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.inventory.domain.InventoryItem;
import com.taller360.app.inventory.domain.WorkOrderPart;
import com.taller360.app.inventory.infrastructure.InventoryItemRepository;
import com.taller360.app.inventory.infrastructure.WorkOrderPartRepository;
import com.taller360.app.labor.domain.LaborItem;
import com.taller360.app.labor.infrastructure.LaborItemRepository;
import com.taller360.app.vehicles.domain.Vehicle;
import com.taller360.app.vehicles.infrastructure.VehicleRepository;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private WorkOrderPartRepository workOrderPartRepository;

    @Autowired
    private LaborItemRepository laborItemRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOperationalDashboard() throws Exception {
        InventoryItem lowStockA = saveInventoryItem("Filtro de aceite", "INV-LOW-A", "2.00", "5.00", true);
        saveInventoryItem("Pastillas de freno", "INV-LOW-B", "5.00", "5.00", false);
        InventoryItem normalStock = saveInventoryItem("Bateria", "INV-OK-A", "8.00", "3.00", true);

        WorkOrder readyOrder = saveWorkOrder(WorkOrderStatus.READY, currentMonthDate(2), null);
        saveUsedPart(readyOrder, lowStockA, "2.00", "100.00", "40.00");
        saveLaborItem(readyOrder, "Revision general", "50.00");

        WorkOrder inProgressOrder = saveWorkOrder(WorkOrderStatus.IN_PROGRESS, currentMonthDate(5), null);
        saveLaborItem(inProgressOrder, "Ajuste", "30.00");

        WorkOrder deliveredOrder = saveWorkOrder(WorkOrderStatus.DELIVERED, currentMonthDate(8), currentMonthDate(10));
        saveUsedPart(deliveredOrder, normalStock, "1.00", "120.00", "70.00");

        WorkOrder cancelledOrder = saveWorkOrder(WorkOrderStatus.CANCELLED, currentMonthDate(12), null);
        saveUsedPart(cancelledOrder, normalStock, "1.00", "999.00", "10.00");

        WorkOrder rejectedOrder = saveWorkOrder(WorkOrderStatus.REJECTED, currentMonthDate(14), null);
        saveLaborItem(rejectedOrder, "No debe contar", "888.00");

        WorkOrder previousMonthApprovedOrder = saveWorkOrder(WorkOrderStatus.APPROVED, previousMonthDate(20), null);
        saveUsedPart(previousMonthApprovedOrder, normalStock, "1.00", "777.00", "20.00");

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWorkOrdersThisMonth").value(5))
                .andExpect(jsonPath("$.workOrdersByStatus.READY").value(1))
                .andExpect(jsonPath("$.workOrdersByStatus.IN_PROGRESS").value(1))
                .andExpect(jsonPath("$.workOrdersByStatus.DELIVERED").value(1))
                .andExpect(jsonPath("$.workOrdersByStatus.CANCELLED").value(1))
                .andExpect(jsonPath("$.workOrdersByStatus.REJECTED").value(1))
                .andExpect(jsonPath("$.workOrdersByStatus.APPROVED").value(1))
                .andExpect(jsonPath("$.estimatedRevenueThisMonth").value(400.00))
                .andExpect(jsonPath("$.pendingWorkOrders").value(3))
                .andExpect(jsonPath("$.readyToDeliverWorkOrders").value(1))
                .andExpect(jsonPath("$.deliveredWorkOrdersThisMonth").value(1))
                .andExpect(jsonPath("$.lowStockItems.length()").value(2))
                .andExpect(jsonPath("$.lowStockItems[0].sku").value("INV-LOW-A"))
                .andExpect(jsonPath("$.lowStockItems[1].sku").value("INV-LOW-B"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldDenyDashboardForReceptionist() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isForbidden());
    }

    private WorkOrder saveWorkOrder(WorkOrderStatus status, LocalDateTime receptionDate, LocalDateTime deliveredAt) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("Dashboard Customer " + suffix);
        customer.setIdentification("DB-" + suffix);
        customer.setPhone("098" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate(("DB" + suffix).substring(0, 6).toUpperCase());
        vehicle.setBrand("Chevrolet");
        vehicle.setModel("D-Max");
        vehicle.setMileage(90000L);
        vehicle = vehicleRepository.save(vehicle);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode("OT-DASH-" + suffix);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(status);
        workOrder.setReceptionDate(receptionDate);
        workOrder.setCurrentMileage(90000L);
        workOrder.setCustomerComplaint("Dashboard flow");
        if (status == WorkOrderStatus.READY || status == WorkOrderStatus.DELIVERED) {
            workOrder.setQualityControlCompleted(true);
            workOrder.setReadyAt(receptionDate.plusDays(1));
        }
        if (status == WorkOrderStatus.DELIVERED) {
            workOrder.setDeliveredAt(deliveredAt);
            workOrder.setDeliveredTo("Cliente dashboard");
            workOrder.setFinalMileage(90010L);
        }
        return workOrderRepository.save(workOrder);
    }

    private InventoryItem saveInventoryItem(String name, String sku, String currentStock, String minStock, boolean active) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setName(name);
        inventoryItem.setSku(sku);
        inventoryItem.setDescription(name + " desc");
        inventoryItem.setCurrentStock(new BigDecimal(currentStock));
        inventoryItem.setMinStock(new BigDecimal(minStock));
        inventoryItem.setUnitCost(new BigDecimal("10.00"));
        inventoryItem.setSalePrice(new BigDecimal("20.00"));
        inventoryItem.setActive(active);
        return inventoryItemRepository.save(inventoryItem);
    }

    private void saveUsedPart(
            WorkOrder workOrder,
            InventoryItem inventoryItem,
            String quantity,
            String salePrice,
            String unitCost
    ) {
        WorkOrderPart workOrderPart = new WorkOrderPart();
        workOrderPart.setWorkOrder(workOrder);
        workOrderPart.setInventoryItem(inventoryItem);
        workOrderPart.setQuantity(new BigDecimal(quantity));
        workOrderPart.setSalePrice(new BigDecimal(salePrice));
        workOrderPart.setUnitCost(new BigDecimal(unitCost));
        workOrderPart.recalculateAmounts();
        workOrderPartRepository.save(workOrderPart);
    }

    private void saveLaborItem(WorkOrder workOrder, String description, String price) {
        LaborItem laborItem = new LaborItem();
        laborItem.setWorkOrder(workOrder);
        laborItem.setDescription(description);
        laborItem.setPrice(new BigDecimal(price));
        laborItemRepository.save(laborItem);
    }

    private LocalDateTime currentMonthDate(int dayOfMonth) {
        YearMonth currentMonth = YearMonth.now();
        return currentMonth.atDay(dayOfMonth).atTime(9, 0);
    }

    private LocalDateTime previousMonthDate(int dayOfMonth) {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        return previousMonth.atDay(dayOfMonth).atTime(9, 0);
    }
}
