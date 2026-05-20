package com.taller360.app.inventory;

import com.taller360.app.Taller360Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateInventoryItemSearchBySkuAndListLowStock() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sku = "SKU-" + suffix;

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Brake Pad %s",
                                  "sku": "%s",
                                  "description": "Front brake pad",
                                  "currentStock": 2,
                                  "minStock": 2,
                                  "unitCost": 15.50,
                                  "salePrice": 28.00
                                }
                                """.formatted(suffix, sku)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(sku))
                .andExpect(jsonPath("$.lowStock").value(true));

        mockMvc.perform(get("/api/inventory").param("search", sku))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value(sku));

        mockMvc.perform(get("/api/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku", hasItem(sku)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateInventoryItem() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        String response = mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Filter %s",
                                  "sku": "FLT-%s",
                                  "currentStock": 10,
                                  "minStock": 1,
                                  "unitCost": 5.00,
                                  "salePrice": 8.00
                                }
                                """.formatted(suffix, suffix)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(patch("/api/inventory/{id}/deactivate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
