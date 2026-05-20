package com.taller360.app.inventory;

import com.jayway.jsonpath.JsonPath;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.inventory.domain.InventoryItem;
import com.taller360.app.inventory.infrastructure.InventoryItemRepository;
import com.taller360.app.vehicles.domain.Vehicle;
import com.taller360.app.vehicles.infrastructure.VehicleRepository;
import com.taller360.app.workorders.domain.WorkOrder;
import com.taller360.app.workorders.domain.WorkOrderStatus;
import com.taller360.app.workorders.infrastructure.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkOrderPartIntegrationTest {

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

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldRegisterUsedPartReduceStockAndCalculateAmounts() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);
        InventoryItem item = seedInventoryItem("PADS", new BigDecimal("10.00"), true);

        mockMvc.perform(post("/api/work-orders/{id}/parts", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inventoryItemId": %d,
                                  "quantity": 2
                                }
                                """.formatted(item.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inventoryItemId").value(item.getId()))
                .andExpect(jsonPath("$.quantity").value(2.00))
                .andExpect(jsonPath("$.total").value(60.00))
                .andExpect(jsonPath("$.margin").value(20.00));

        InventoryItem updatedItem = inventoryItemRepository.findById(item.getId()).orElseThrow();
        assertDecimal(updatedItem.getCurrentStock(), "8.00");

        mockMvc.perform(get("/api/work-orders/{id}/parts", workOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryItemSku").value(item.getSku()));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldRestoreStockWhenDeletingUsedPartBeforeDelivery() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.IN_PROGRESS);
        InventoryItem item = seedInventoryItem("OIL", new BigDecimal("5.00"), true);

        MvcResult result = mockMvc.perform(post("/api/work-orders/{id}/parts", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inventoryItemId": %d,
                                  "quantity": 2
                                }
                                """.formatted(item.getId())))
                .andExpect(status().isCreated())
                .andReturn();

        Long partId = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(delete("/api/work-orders/{id}/parts/{partId}", workOrder.getId(), partId))
                .andExpect(status().isNoContent());

        InventoryItem updatedItem = inventoryItemRepository.findById(item.getId()).orElseThrow();
        assertDecimal(updatedItem.getCurrentStock(), "5.00");
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotRegisterUsedPartIfStockIsInsufficient() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);
        InventoryItem item = seedInventoryItem("BELT", new BigDecimal("1.00"), true);

        mockMvc.perform(post("/api/work-orders/{id}/parts", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inventoryItemId": %d,
                                  "quantity": 2
                                }
                                """.formatted(item.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Stock cannot be reduced if there is not enough quantity"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotRegisterUsedPartIfInventoryItemIsInactive() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);
        InventoryItem item = seedInventoryItem("FILTER", new BigDecimal("5.00"), false);

        mockMvc.perform(post("/api/work-orders/{id}/parts", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inventoryItemId": %d,
                                  "quantity": 1
                                }
                                """.formatted(item.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("An inactive inventory item cannot be used in new work orders"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotRegisterUsedPartOnDeliveredWorkOrder() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.DELIVERED);
        InventoryItem item = seedInventoryItem("SPARK", new BigDecimal("5.00"), true);

        mockMvc.perform(post("/api/work-orders/{id}/parts", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inventoryItemId": %d,
                                  "quantity": 1
                                }
                                """.formatted(item.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parts can only be added when the work order is APPROVED or IN_PROGRESS"));
    }

    private WorkOrder seedWorkOrder(WorkOrderStatus status) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("Inventory Customer " + suffix);
        customer.setIdentification("INV-" + suffix);
        customer.setPhone("097" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("INV-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Hyundai");
        vehicle.setModel("Accent");
        vehicle.setMileage(70000L);
        vehicle = vehicleRepository.save(vehicle);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode("OT-INV-" + suffix);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(status);
        workOrder.setReceptionDate(LocalDateTime.now());
        workOrder.setCurrentMileage(70000L);
        workOrder.setCustomerComplaint("Engine issue");
        workOrder.setQualityControlCompleted(false);
        return workOrderRepository.save(workOrder);
    }

    private InventoryItem seedInventoryItem(String prefix, BigDecimal stock, boolean active) {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        InventoryItem item = new InventoryItem();
        item.setName(prefix + " " + suffix);
        item.setSku(prefix + "-" + suffix);
        item.setCurrentStock(stock.setScale(2));
        item.setMinStock(new BigDecimal("1.00"));
        item.setUnitCost(new BigDecimal("20.00"));
        item.setSalePrice(new BigDecimal("30.00"));
        item.setActive(active);
        return inventoryItemRepository.save(item);
    }

    private void assertDecimal(BigDecimal actual, String expected) {
        if (actual.compareTo(new BigDecimal(expected)) != 0) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
