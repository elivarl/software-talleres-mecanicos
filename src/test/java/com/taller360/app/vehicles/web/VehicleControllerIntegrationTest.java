package com.taller360.app.vehicles.web;

import com.taller360.app.Taller360Application;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.inspections.domain.InspectionPhoto;
import com.taller360.app.inspections.domain.ReceptionInspection;
import com.taller360.app.inspections.infrastructure.ReceptionInspectionRepository;
import com.taller360.app.inventory.domain.InventoryItem;
import com.taller360.app.inventory.domain.WorkOrderPart;
import com.taller360.app.inventory.infrastructure.InventoryItemRepository;
import com.taller360.app.inventory.infrastructure.WorkOrderPartRepository;
import com.taller360.app.labor.domain.LaborItem;
import com.taller360.app.labor.infrastructure.LaborItemRepository;
import com.taller360.app.quotations.domain.Quotation;
import com.taller360.app.quotations.domain.QuotationItem;
import com.taller360.app.quotations.domain.QuotationItemType;
import com.taller360.app.quotations.domain.QuotationStatus;
import com.taller360.app.quotations.infrastructure.QuotationRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
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

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ReceptionInspectionRepository receptionInspectionRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private WorkOrderPartRepository workOrderPartRepository;

    @Autowired
    private LaborItemRepository laborItemRepository;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldCreateVehicleAndReturnFullHistoryById() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer();
        customer.setFullName("Andrea Castillo " + suffix);
        customer.setIdentification("CI-" + suffix);
        customer.setPhone("098" + suffix);
        customer.setEmail("andrea." + suffix + "@test.com");
        customer.setAddress("Guayaquil");
        customer = customerRepository.save(customer);

        String plate = "PBC-" + suffix.substring(0, 4).toUpperCase();

        String vehicleResponse = mockMvc.perform(post("/api/vehicles")
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
                .andExpect(jsonPath("$.customerFullName").value("Andrea Castillo " + suffix))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer vehicleId = com.jayway.jsonpath.JsonPath.read(vehicleResponse, "$.id");
        Vehicle vehicle = vehicleRepository.findById(vehicleId.longValue()).orElseThrow();

        seedVehicleHistory(customer, vehicle, suffix);

        mockMvc.perform(get("/api/vehicles/{id}/history", vehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.plate").value(plate))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.customer.fullName").value("Andrea Castillo " + suffix))
                .andExpect(jsonPath("$.workOrders").isArray())
                .andExpect(jsonPath("$.workOrders", hasSize(2)))
                .andExpect(jsonPath("$.workOrders[0].workOrderCode").value("OT-HIST-NEW-" + suffix))
                .andExpect(jsonPath("$.workOrders[0].status").value("DELIVERED"))
                .andExpect(jsonPath("$.workOrders[0].diagnosis").value("Front brake pads replaced"))
                .andExpect(jsonPath("$.workOrders[0].deliveredTo").value("Andrea Castillo"))
                .andExpect(jsonPath("$.workOrders[0].currentMileage").value(80321))
                .andExpect(jsonPath("$.workOrders[0].finalMileage").value(80400))
                .andExpect(jsonPath("$.workOrders[0].quotation.code").value("COT-HIST-" + suffix))
                .andExpect(jsonPath("$.workOrders[0].quotation.status").value("APPROVED"))
                .andExpect(jsonPath("$.workOrders[0].quotation.items", hasSize(2)))
                .andExpect(jsonPath("$.workOrders[0].inspection.photos", hasSize(1)))
                .andExpect(jsonPath("$.workOrders[0].usedParts", hasSize(1)))
                .andExpect(jsonPath("$.workOrders[0].usedParts[0].inventoryItemSku").value("BP-" + suffix))
                .andExpect(jsonPath("$.workOrders[0].laborItems", hasSize(1)))
                .andExpect(jsonPath("$.workOrders[0].laborItems[0].description").value("Brake pad replacement labor"))
                .andExpect(jsonPath("$.workOrders[0].totals.partsTotal").value(30.00))
                .andExpect(jsonPath("$.workOrders[0].totals.laborTotal").value(25.00))
                .andExpect(jsonPath("$.workOrders[0].totals.serviceTotal").value(55.00))
                .andExpect(jsonPath("$.workOrders[0].totals.quotationTotal").value(55.00))
                .andExpect(jsonPath("$.workOrders[0].internalNotes").doesNotExist())
                .andExpect(jsonPath("$.workOrders[1].workOrderCode").value("OT-HIST-OLD-" + suffix))
                .andExpect(jsonPath("$.workOrders[1].status").value("REJECTED"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldReturnFullHistoryByPlateAndOnlyForRequestedVehicle() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer();
        customer.setFullName("Carlos Vera " + suffix);
        customer.setIdentification("VH-" + suffix);
        customer.setPhone("094" + suffix);
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setPlate("GYE-" + suffix.substring(0, 4).toUpperCase());
        vehicle.setBrand("Chevrolet");
        vehicle.setModel("Sail");
        vehicle.setMileage(50000L);
        vehicle = vehicleRepository.save(vehicle);

        seedVehicleHistory(customer, vehicle, suffix);

        mockMvc.perform(get("/api/vehicles/by-plate/{plate}/history", vehicle.getPlate().toLowerCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.id").value(vehicle.getId()))
                .andExpect(jsonPath("$.workOrders", hasSize(2)))
                .andExpect(jsonPath("$.workOrders[0].workOrderCode").value("OT-HIST-NEW-" + suffix))
                .andExpect(jsonPath("$.workOrders[1].workOrderCode").value("OT-HIST-OLD-" + suffix));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void shouldReturn404WhenVehicleHistoryByPlateDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/vehicles/by-plate/{plate}/history", "ZZZ-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Vehicle not found"));
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

    private void seedVehicleHistory(Customer customer, Vehicle vehicle, String suffix) {
        WorkOrder newerWorkOrder = new WorkOrder();
        newerWorkOrder.setCode("OT-HIST-NEW-" + suffix);
        newerWorkOrder.setCustomer(customer);
        newerWorkOrder.setVehicle(vehicle);
        newerWorkOrder.setStatus(WorkOrderStatus.DELIVERED);
        newerWorkOrder.setReceptionDate(LocalDateTime.now().minusDays(1));
        newerWorkOrder.setEstimatedDeliveryDate(LocalDate.now().plusDays(1));
        newerWorkOrder.setCurrentMileage(vehicle.getMileage());
        newerWorkOrder.setFuelLevel("Half");
        newerWorkOrder.setCustomerComplaint("Brake noise");
        newerWorkOrder.setInitialObservations("Vehicle received in normal condition");
        newerWorkOrder.setDiagnosis("Front brake pads replaced");
        newerWorkOrder.setInternalNotes("Sensitive internal note");
        newerWorkOrder.setQualityControlCompleted(true);
        newerWorkOrder.setQualityControlNotes("Road test approved");
        newerWorkOrder.setReadyAt(LocalDateTime.now().minusHours(4));
        newerWorkOrder.setDeliveredAt(LocalDateTime.now().minusHours(1));
        newerWorkOrder.setDeliveredTo("Andrea Castillo");
        newerWorkOrder.setFinalMileage(vehicle.getMileage() + 79);
        newerWorkOrder = workOrderRepository.save(newerWorkOrder);

        WorkOrder olderWorkOrder = new WorkOrder();
        olderWorkOrder.setCode("OT-HIST-OLD-" + suffix);
        olderWorkOrder.setCustomer(customer);
        olderWorkOrder.setVehicle(vehicle);
        olderWorkOrder.setStatus(WorkOrderStatus.REJECTED);
        olderWorkOrder.setReceptionDate(LocalDateTime.now().minusDays(30));
        olderWorkOrder.setEstimatedDeliveryDate(LocalDate.now().minusDays(28));
        olderWorkOrder.setCurrentMileage(vehicle.getMileage() - 1000);
        olderWorkOrder.setFuelLevel("Low");
        olderWorkOrder.setCustomerComplaint("Suspension noise");
        olderWorkOrder.setDiagnosis("Shock absorber wear detected");
        olderWorkOrder = workOrderRepository.save(olderWorkOrder);

        Vehicle otherVehicle = new Vehicle();
        otherVehicle.setCustomer(customer);
        otherVehicle.setPlate("OTH-" + suffix.substring(0, 4).toUpperCase());
        otherVehicle.setBrand("Nissan");
        otherVehicle.setModel("Frontier");
        otherVehicle.setMileage(60000L);
        otherVehicle = vehicleRepository.save(otherVehicle);

        WorkOrder unrelatedWorkOrder = new WorkOrder();
        unrelatedWorkOrder.setCode("OT-OTH-" + suffix);
        unrelatedWorkOrder.setCustomer(customer);
        unrelatedWorkOrder.setVehicle(otherVehicle);
        unrelatedWorkOrder.setStatus(WorkOrderStatus.RECEIVED);
        unrelatedWorkOrder.setReceptionDate(LocalDateTime.now());
        unrelatedWorkOrder.setCurrentMileage(60000L);
        unrelatedWorkOrder.setCustomerComplaint("Unrelated vehicle");
        workOrderRepository.save(unrelatedWorkOrder);

        ReceptionInspection inspection = new ReceptionInspection();
        inspection.setWorkOrder(newerWorkOrder);
        inspection.setMileage(vehicle.getMileage());
        inspection.setFuelLevel("Half");
        inspection.setExteriorCondition("Good");
        inspection.setVisibleScratches("Minor scratch on rear bumper");
        inspection.setVisibleDents("None");
        inspection.setLightsWorking(true);
        inspection.setTiresCondition("Good");
        inspection.setMirrorsCondition("Good");
        inspection.setHasSpareTire(true);
        inspection.setHasJack(true);
        inspection.setHasTools(false);
        inspection.setHasDocuments(true);
        inspection.setGeneralNotes("Vehicle received normally");

        InspectionPhoto photo = new InspectionPhoto();
        photo.setPhotoUrl("https://example.com/photos/brakes-" + suffix + ".jpg");
        photo.setDescription("Front brakes");
        inspection.addPhoto(photo);
        receptionInspectionRepository.save(inspection);

        Quotation quotation = new Quotation();
        quotation.setCode("COT-HIST-" + suffix);
        quotation.setWorkOrder(newerWorkOrder);
        quotation.setStatus(QuotationStatus.DRAFT);

        QuotationItem partItem = new QuotationItem();
        partItem.setType(QuotationItemType.PART);
        partItem.setDescription("Front brake pad set");
        partItem.setQuantity(new BigDecimal("1.00"));
        partItem.setUnitPrice(new BigDecimal("30.00"));
        partItem.recalculateTotal();
        quotation.addItem(partItem);

        QuotationItem laborItem = new QuotationItem();
        laborItem.setType(QuotationItemType.LABOR);
        laborItem.setDescription("Brake pad replacement labor");
        laborItem.setQuantity(new BigDecimal("1.00"));
        laborItem.setUnitPrice(new BigDecimal("25.00"));
        laborItem.recalculateTotal();
        quotation.addItem(laborItem);

        quotation.setStatus(QuotationStatus.APPROVED);
        quotation.setSubtotal(new BigDecimal("47.83"));
        quotation.setTax(new BigDecimal("7.17"));
        quotation.setTotal(new BigDecimal("55.00"));
        quotation.setPublicToken("token-" + suffix);
        quotation.setSentAt(LocalDateTime.now().minusHours(10));
        quotation.setCustomerDecisionAt(LocalDateTime.now().minusHours(9));
        quotationRepository.save(quotation);

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setName("Brake Pad Set " + suffix);
        inventoryItem.setSku("BP-" + suffix);
        inventoryItem.setCurrentStock(new BigDecimal("10.00"));
        inventoryItem.setMinStock(new BigDecimal("1.00"));
        inventoryItem.setUnitCost(new BigDecimal("20.00"));
        inventoryItem.setSalePrice(new BigDecimal("30.00"));
        inventoryItem.setActive(true);
        inventoryItem = inventoryItemRepository.save(inventoryItem);

        WorkOrderPart workOrderPart = new WorkOrderPart();
        workOrderPart.setWorkOrder(newerWorkOrder);
        workOrderPart.setInventoryItem(inventoryItem);
        workOrderPart.setQuantity(new BigDecimal("1.00"));
        workOrderPart.setUnitCost(new BigDecimal("20.00"));
        workOrderPart.setSalePrice(new BigDecimal("30.00"));
        workOrderPart.recalculateAmounts();
        workOrderPartRepository.save(workOrderPart);

        LaborItem laborItemEntity = new LaborItem();
        laborItemEntity.setWorkOrder(newerWorkOrder);
        laborItemEntity.setDescription("Brake pad replacement labor");
        laborItemEntity.setPrice(new BigDecimal("25.00"));
        laborItemRepository.save(laborItemEntity);
    }
}
