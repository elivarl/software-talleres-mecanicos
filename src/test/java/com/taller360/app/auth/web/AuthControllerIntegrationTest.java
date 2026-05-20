package com.taller360.app.auth.web;

import com.taller360.app.Taller360Application;
import com.taller360.app.users.domain.User;
import com.taller360.app.users.domain.UserRole;
import com.taller360.app.users.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Taller360Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldLoginWithSeedAdminUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@taller360.com",
                                  "password": "Admin12345*"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.user.email").value("admin@taller360.com"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }

    @Test
    void shouldNotLoginDisabledUser() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        User user = new User();
        user.setFullName("Disabled User " + suffix);
        user.setEmail("disabled." + suffix + "@test.com");
        user.setPassword(passwordEncoder.encode("Disabled123*"));
        user.setRole(UserRole.RECEPTIONIST);
        user.setActive(false);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Disabled123*"
                                }
                                """.formatted(user.getEmail())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User is disabled"));
    }
}
