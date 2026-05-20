package com.taller360.app.workorders.web;

import com.taller360.app.Taller360Application;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkOrderQualityControlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldCompleteQualityControlAndMarkWorkOrderReady() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/work-orders/{id}/quality-control", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "completed": true,
                                  "notes": "Road test completed. Braking system working correctly."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qualityControlCompleted").value(true))
                .andExpect(jsonPath("$.qualityControlNotes").value("Road test completed. Braking system working correctly."));

        mockMvc.perform(patch("/api/work-orders/{id}/mark-ready", workOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.readyAt").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotCompleteQualityControlIfWorkOrderIsNotInProgress() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.APPROVED);

        mockMvc.perform(patch("/api/work-orders/{id}/quality-control", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "completed": true,
                                  "notes": "Checklist completed"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Quality control can only be completed if work order is IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotMarkWorkOrderReadyWithoutCompletedQualityControl() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/work-orders/{id}/mark-ready", workOrder.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot move to READY unless quality control is completed"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldNotModifyQualityControlAfterDelivered() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.DELIVERED);

        mockMvc.perform(patch("/api/work-orders/{id}/quality-control", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "completed": true,
                                  "notes": "Late update"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Quality control cannot be modified after DELIVERED"));
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void shouldRejectFalseCompletedFlag() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/work-orders/{id}/quality-control", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "completed": false,
                                  "notes": "Not allowed"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Quality control can only be marked as completed"));
    }

    private WorkOrder seedWorkOrder(WorkOrderStatus status) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("QC Customer " + suffix);
        customer.setIdentification("QC-" + suffix);
        customer.setPhone("096" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("QC-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Mazda");
        vehicle.setModel("BT-50");
        vehicle.setMileage(88000L);
        vehicle = vehicleRepository.save(vehicle);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode("OT-QC-" + suffix);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(status);
        workOrder.setReceptionDate(LocalDateTime.now());
        workOrder.setCurrentMileage(88000L);
        workOrder.setCustomerComplaint("Brake vibration");
        workOrder.setQualityControlCompleted(false);
        return workOrderRepository.save(workOrder);
    }
}
