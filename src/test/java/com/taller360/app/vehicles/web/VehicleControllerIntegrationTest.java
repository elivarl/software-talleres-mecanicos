package com.taller360.app.vehicles.web;

import com.taller360.app.Taller360Application;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldCreateVehicleAndReturnBaseHistoryByPlate() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer();
        customer.setFullName("Andrea Castillo " + suffix);
        customer.setIdentification("CI-" + suffix);
        customer.setPhone("098" + suffix);
        customer.setEmail("andrea." + suffix + "@test.com");
        customer.setAddress("Guayaquil");
        customer = customerRepository.save(customer);

        String plate = "PBC-" + suffix.substring(0, 4).toUpperCase();

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d,
                                  "plate": "%s",
                                  "brand": "Toyota",
                                  "model": "Corolla",
                                  "year": 2020,
                                  "color": "Silver",
                                  "vin": "VIN-%s",
                                  "mileage": 80321
                                }
                                """.formatted(customer.getId(), plate, suffix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.customerFullName").value("Andrea Castillo " + suffix));

        mockMvc.perform(get("/api/vehicles/by-plate/{plate}/history", plate.toLowerCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.plate").value(plate))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.customer.fullName").value("Andrea Castillo " + suffix))
                .andExpect(jsonPath("$.workOrders").isArray())
                .andExpect(jsonPath("$.workOrders").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectVehicleWithUnreasonableYear() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer();
        customer.setFullName("Luis Ortega " + suffix);
        customer.setPhone("097" + suffix);
        customer = customerRepository.save(customer);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d,
                                  "plate": "TAA-%s",
                                  "brand": "Mazda",
                                  "model": "BT-50",
                                  "year": 1899,
                                  "mileage": 1000
                                }
                                """.formatted(customer.getId(), suffix.substring(0, 4).toUpperCase())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Year must be between 1900")));
    }
}
