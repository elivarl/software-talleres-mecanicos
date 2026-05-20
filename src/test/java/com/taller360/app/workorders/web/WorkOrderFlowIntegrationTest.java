package com.taller360.app.workorders.web;

import com.jayway.jsonpath.JsonPath;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.users.domain.User;
import com.taller360.app.users.domain.UserRole;
import com.taller360.app.users.infrastructure.UserRepository;
import com.taller360.app.vehicles.domain.Vehicle;
import com.taller360.app.vehicles.infrastructure.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkOrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldCreateWorkOrderRegisterDiagnosisAndManageInspection() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("Rocio Paz " + suffix);
        customer.setIdentification("CI-" + suffix);
        customer.setPhone("099" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("GYE-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Chevrolet");
        vehicle.setModel("D-Max");
        vehicle.setMileage(145000L);
        vehicle = vehicleRepository.save(vehicle);

        User mechanic = new User();
        mechanic.setFullName("Mechanic " + suffix);
        mechanic.setEmail("mechanic." + suffix + "@test.com");
        mechanic.setPassword(passwordEncoder.encode("Mechanic123*"));
        mechanic.setRole(UserRole.MECHANIC);
        mechanic.setActive(true);
        mechanic = userRepository.save(mechanic);

        MvcResult createWorkOrderResult = mockMvc.perform(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d,
                                  "vehicleId": %d,
                                  "assignedMechanicId": %d,
                                  "currentMileage": 145000,
                                  "fuelLevel": "Half",
                                  "customerComplaint": "Noise when braking",
                                  "initialObservations": "Vehicle enters under own power"
                                }
                                """.formatted(customer.getId(), vehicle.getId(), mechanic.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", matchesPattern("OT-\\d{6}")))
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andExpect(jsonPath("$.assignedMechanicId").value(mechanic.getId()))
                .andReturn();

        Long workOrderId = ((Number) JsonPath.read(createWorkOrderResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(patch("/api/work-orders/{id}/diagnosis", workOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diagnosis": "Front brake pads are worn out"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DIAGNOSIS"))
                .andExpect(jsonPath("$.diagnosis").value("Front brake pads are worn out"));

        MvcResult createInspectionResult = mockMvc.perform(post("/api/work-orders/{id}/inspection", workOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mileage": 145000,
                                  "fuelLevel": "Half",
                                  "exteriorCondition": "Good",
                                  "visibleScratches": "Minor scratch on rear bumper",
                                  "visibleDents": "None",
                                  "lightsWorking": true,
                                  "tiresCondition": "Good",
                                  "mirrorsCondition": "Good",
                                  "hasSpareTire": true,
                                  "hasJack": true,
                                  "hasTools": false,
                                  "hasDocuments": true,
                                  "generalNotes": "Vehicle received normally"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workOrderId").value(workOrderId))
                .andExpect(jsonPath("$.photos").isArray())
                .andExpect(jsonPath("$.photos").isEmpty())
                .andReturn();

        Long inspectionId = ((Number) JsonPath.read(createInspectionResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(post("/api/inspections/{id}/photos", inspectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "photoUrl": "https://example.com/photos/front-view.jpg",
                                  "description": "Front view"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.photos[0].photoUrl").value("https://example.com/photos/front-view.jpg"));

        mockMvc.perform(get("/api/work-orders/{id}/inspection", workOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photos[0].description").value("Front view"));
    }
}
