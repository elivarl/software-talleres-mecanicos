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
class WorkOrderDeliveryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldDeliverReadyWorkOrder() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.READY, 88000L);

        mockMvc.perform(patch("/api/work-orders/{id}/deliver", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveredTo": "Juan Perez",
                                  "finalMileage": 88010
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.deliveredTo").value("Juan Perez"))
                .andExpect(jsonPath("$.finalMileage").value(88010))
                .andExpect(jsonPath("$.deliveredAt").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldNotDeliverWorkOrderIfItIsInProgress() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.IN_PROGRESS, 88000L);

        mockMvc.perform(patch("/api/work-orders/{id}/deliver", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveredTo": "Juan Perez",
                                  "finalMileage": 88010
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot deliver a work order that is not ready"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldNotDeliverWorkOrderIfFinalMileageIsLowerThanCurrentMileage() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.READY, 88000L);

        mockMvc.perform(patch("/api/work-orders/{id}/deliver", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveredTo": "Juan Perez",
                                  "finalMileage": 87999
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Final mileage must be greater than or equal to current mileage"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldRequireDeliveredTo() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.READY, 88000L);

        mockMvc.perform(patch("/api/work-orders/{id}/deliver", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveredTo": " ",
                                  "finalMileage": 88010
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldNotDeliverRejectedWorkOrder() throws Exception {
        WorkOrder workOrder = seedWorkOrder(WorkOrderStatus.REJECTED, 88000L);

        mockMvc.perform(patch("/api/work-orders/{id}/deliver", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveredTo": "Juan Perez",
                                  "finalMileage": 88010
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot deliver a work order that is not ready"));
    }

    private WorkOrder seedWorkOrder(WorkOrderStatus status, Long currentMileage) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("Delivery Customer " + suffix);
        customer.setIdentification("DEL-" + suffix);
        customer.setPhone("095" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("DEL-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Toyota");
        vehicle.setModel("Hilux");
        vehicle.setMileage(currentMileage);
        vehicle = vehicleRepository.save(vehicle);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode("OT-DEL-" + suffix);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(status);
        workOrder.setReceptionDate(LocalDateTime.now());
        workOrder.setCurrentMileage(currentMileage);
        workOrder.setCustomerComplaint("General maintenance");
        workOrder.setQualityControlCompleted(status == WorkOrderStatus.READY || status == WorkOrderStatus.DELIVERED);
        if (status == WorkOrderStatus.READY) {
            workOrder.setReadyAt(LocalDateTime.now());
        }
        return workOrderRepository.save(workOrder);
    }
}
