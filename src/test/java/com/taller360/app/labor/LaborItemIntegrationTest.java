package com.taller360.app.labor;

import com.jayway.jsonpath.JsonPath;
import com.taller360.app.Taller360Application;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
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

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LaborItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private LaborItemRepository laborItemRepository;

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldRegisterLaborItemWhenWorkOrderIsApproved() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);

        mockMvc.perform(post("/api/work-orders/{id}/labor", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Front brake pad replacement",
                                  "price": 45
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workOrderId").value(workOrder.getId()))
                .andExpect(jsonPath("$.description").value("Front brake pad replacement"))
                .andExpect(jsonPath("$.price").value(45.00));

        mockMvc.perform(get("/api/work-orders/{id}/labor", workOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Front brake pad replacement"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotRegisterLaborItemWhenWorkOrderIsQuoted() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.QUOTED);

        mockMvc.perform(post("/api/work-orders/{id}/labor", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Alignment service",
                                  "price": 20
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Labor can only be added when the work order is APPROVED or IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotRegisterLaborItemOnDeliveredWorkOrder() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.DELIVERED);

        mockMvc.perform(post("/api/work-orders/{id}/labor", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Oil change labor",
                                  "price": 15
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Labor can only be added when the work order is APPROVED or IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotDeleteLaborItemFromDeliveredWorkOrder() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.IN_PROGRESS);

        MvcResult result = mockMvc.perform(post("/api/work-orders/{id}/labor", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Suspension diagnosis labor",
                                  "price": 35
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Long laborId = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();

        workOrder.setStatus(WorkOrderStatus.DELIVERED);
        workOrderRepository.save(workOrder);

        mockMvc.perform(delete("/api/work-orders/{id}/labor/{laborId}", workOrder.getId(), laborId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Labor cannot be deleted from a DELIVERED work order"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldValidateDescriptionAndPrice() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);

        mockMvc.perform(post("/api/work-orders/{id}/labor", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": " ",
                                  "price": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors[0]").exists())
                .andExpect(jsonPath("$.validationErrors[1]").exists());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldDeleteLaborItemWhenWorkOrderIsNotDelivered() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);

        MvcResult result = mockMvc.perform(post("/api/work-orders/{id}/labor", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Battery replacement labor",
                                  "price": 18
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Long laborId = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(delete("/api/work-orders/{id}/labor/{laborId}", workOrder.getId(), laborId))
                .andExpect(status().isNoContent());

        if (laborItemRepository.findById(laborId).isPresent()) {
            throw new AssertionError("Labor item should have been deleted");
        }
    }

    private WorkOrder seedWorkOrder(WorkOrderStatus status) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("Labor Customer " + suffix);
        customer.setIdentification("LAB-" + suffix);
        customer.setPhone("098" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("LAB-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Kia");
        vehicle.setModel("Rio");
        vehicle.setMileage(52000L);
        vehicle = vehicleRepository.save(vehicle);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode("OT-LAB-" + suffix);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(status);
        workOrder.setReceptionDate(LocalDateTime.now());
        workOrder.setCurrentMileage(52000L);
        workOrder.setCustomerComplaint("Noise in suspension");
        workOrder.setQualityControlCompleted(false);
        return workOrderRepository.save(workOrder);
    }
}
