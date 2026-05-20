package com.taller360.app.customers.web;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCustomerAndSearchByPhone() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String phone = "099" + suffix;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Carlos Mena %s",
                                  "identification": "ID-%s",
                                  "phone": "%s",
                                  "email": "carlos.%s@test.com",
                                  "address": "Quito"
                                }
                                """.formatted(suffix, suffix, phone, suffix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Carlos Mena " + suffix))
                .andExpect(jsonPath("$.phone").value(phone));

        mockMvc.perform(get("/api/customers").param("search", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phone").value(phone))
                .andExpect(jsonPath("$[0].identification").value("ID-" + suffix));
    }
}
