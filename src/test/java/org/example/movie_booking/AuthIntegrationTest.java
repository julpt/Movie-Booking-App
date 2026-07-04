package org.example.movie_booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.movie_booking.model.dto.RegisterRequest;
import org.example.movie_booking.model.entities.User;
import org.example.movie_booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser_savesWithEncodedPasswordAndDefaultRole() throws Exception {
        RegisterRequest request = new RegisterRequest("Test User", "testuser123", "test123@example.com", "plainPassword");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Optional<User> savedUser = userRepository.findByUsername("testuser123");

        assertTrue(savedUser.isPresent());
        assertNotEquals("plainPassword", savedUser.get().getPassword()); // parola nu e stocata in clar
        assertTrue(passwordEncoder.matches("plainPassword", savedUser.get().getPassword())); // dar hash-ul corespunde
        assertEquals(org.example.movie_booking.model.entities.Role.USER, savedUser.get().getRole()); // rol implicit corect
    }
}