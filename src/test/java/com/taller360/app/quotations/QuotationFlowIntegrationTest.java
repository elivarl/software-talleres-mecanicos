package com.taller360.app.quotations;

import com.jayway.jsonpath.JsonPath;
import com.taller360.app.Taller360Application;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.quotations.domain.Quotation;
import com.taller360.app.quotations.domain.QuotationItem;
import com.taller360.app.quotations.domain.QuotationItemType;
import com.taller360.app.quotations.domain.QuotationStatus;
import com.taller360.app.quotations.infrastructure.QuotationRepository;
import com.taller360.app.users.domain.User;
import com.taller360.app.users.domain.UserRole;
import com.taller360.app.users.infrastructure.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuotationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldCreateDraftQuotationAddItemsSendAndMoveWorkOrderToQuoted() throws Exception {
        WorkOrder workOrder = seedWorkOrder();

        MvcResult createQuotationResult = mockMvc.perform(post("/api/work-orders/{id}/quotation", workOrder.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", matchesPattern("COT-\\d{6}")))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        Long quotationId = ((Number) JsonPath.read(createQuotationResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(post("/api/quotations/{id}/items", quotationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "PART",
                                  "description": "Brake pads",
                                  "quantity": 2,
                                  "unitPrice": 35.50
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(71.00));

        mockMvc.perform(post("/api/quotations/{id}/items", quotationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "LABOR",
                                  "description": "Brake service",
                                  "quantity": 1,
                                  "unitPrice": 20.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(91.00))
                .andExpect(jsonPath("$.tax").value(13.65))
                .andExpect(jsonPath("$.total").value(104.65));

        mockMvc.perform(post("/api/quotations/{id}/send", quotationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.publicToken").isNotEmpty())
                .andExpect(jsonPath("$.subtotal").value(91.00))
                .andExpect(jsonPath("$.tax").value(13.65))
                .andExpect(jsonPath("$.total").value(104.65));

        mockMvc.perform(get("/api/work-orders/{id}", workOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("QUOTED"));
    }

    @Test
    void shouldAllowPublicApprovalWithoutJwtAndMoveWorkOrderToApproved() throws Exception {
        Quotation quotation = seedSentQuotation();

        mockMvc.perform(get("/api/public/quotations/{token}", quotation.getPublicToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.publicToken").value(quotation.getPublicToken()));

        mockMvc.perform(post("/api/public/quotations/{token}/approve", quotation.getPublicToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(post("/api/public/quotations/{token}/approve", quotation.getPublicToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A quotation cannot be approved twice"));

        mockMvc.perform(get("/api/work-orders/{id}", quotation.getWorkOrder().getId()))
                .andExpect(status().isUnauthorized());

        WorkOrder reloaded = workOrderRepository.findById(quotation.getWorkOrder().getId()).orElseThrow();
        assertStatus(reloaded, WorkOrderStatus.APPROVED);
    }

    @Test
    void shouldAllowPublicRejectionWithoutJwtAndMoveWorkOrderToRejected() throws Exception {
        Quotation quotation = seedSentQuotation();

        mockMvc.perform(post("/api/public/quotations/{token}/reject", quotation.getPublicToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        mockMvc.perform(post("/api/public/quotations/{token}/approve", quotation.getPublicToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A quotation cannot be approved after being rejected"));

        WorkOrder reloaded = workOrderRepository.findById(quotation.getWorkOrder().getId()).orElseThrow();
        assertStatus(reloaded, WorkOrderStatus.REJECTED);
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldNotSendQuotationWithoutItems() throws Exception {
        WorkOrder workOrder = seedWorkOrder();

        MvcResult createQuotationResult = mockMvc.perform(post("/api/work-orders/{id}/quotation", workOrder.getId()))
                .andExpect(status().isCreated())
                .andReturn();

        Long quotationId = ((Number) JsonPath.read(createQuotationResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(post("/api/quotations/{id}/send", quotationId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A quotation without items cannot be sent"));
    }

    @Test
    void shouldRequireAuthenticationForPrivateQuotationEndpoints() throws Exception {
        WorkOrder workOrder = seedWorkOrder();

        mockMvc.perform(post("/api/work-orders/{id}/quotation", workOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    private WorkOrder seedWorkOrder() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Customer customer = new Customer();
        customer.setFullName("Quotation Customer " + suffix);
        customer.setIdentification("CI-" + suffix);
        customer.setPhone("098" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("QT-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Kia");
        vehicle.setModel("Rio");
        vehicle.setMileage(50000L);
        vehicle = vehicleRepository.save(vehicle);

        User mechanic = new User();
        mechanic.setFullName("Quotation Mechanic " + suffix);
        mechanic.setEmail("quotation.mechanic." + suffix + "@test.com");
        mechanic.setPassword(passwordEncoder.encode("Mechanic123*"));
        mechanic.setRole(UserRole.MECHANIC);
        mechanic.setActive(true);
        mechanic = userRepository.save(mechanic);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode("OT-MANUAL-" + suffix);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setAssignedMechanic(mechanic);
        workOrder.setStatus(WorkOrderStatus.DIAGNOSIS);
        workOrder.setReceptionDate(LocalDateTime.now());
        workOrder.setCurrentMileage(50000L);
        workOrder.setFuelLevel("Half");
        workOrder.setCustomerComplaint("Brake issue");
        workOrder.setDiagnosis("Brake pads worn");
        workOrder.setQualityControlCompleted(false);
        return workOrderRepository.save(workOrder);
    }

    private Quotation seedSentQuotation() {
        WorkOrder workOrder = seedWorkOrder();

        Quotation quotation = new Quotation();
        quotation.setCode("COT-MANUAL-" + UUID.randomUUID().toString().substring(0, 8));
        quotation.setWorkOrder(workOrder);
        quotation.setStatus(QuotationStatus.DRAFT);
        quotation.recalculateTotals(new BigDecimal("0.15"));

        QuotationItem item = new QuotationItem();
        item.setType(QuotationItemType.LABOR);
        item.setDescription("General service");
        item.setQuantity(new BigDecimal("1.00"));
        item.setUnitPrice(new BigDecimal("100.00"));
        item.recalculateTotal();
        quotation.addItem(item);
        quotation.send(new BigDecimal("0.15"));
        workOrder.markQuotedFromQuotationSent();
        workOrderRepository.save(workOrder);

        return quotationRepository.save(quotation);
    }

    private void assertStatus(WorkOrder workOrder, WorkOrderStatus status) {
        if (workOrder.getStatus() != status) {
            throw new AssertionError("Expected work order status " + status + " but was " + workOrder.getStatus());
        }
    }
}
