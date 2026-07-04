package org.example.movie_booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.movie_booking.model.dto.CinemaRequest;
import org.example.movie_booking.model.dto.ScreenRequest;
import org.example.movie_booking.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CinemaScreenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private SeatRepository seatRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCinemaAndScreen_generatesSeatsAutomatically() throws Exception {
        CinemaRequest cinemaRequest = new CinemaRequest("Cinema City", "Bucharest", "Str. Exemplu 1");

        String cinemaJson = mockMvc.perform(post("/api/cinemas")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cinemaRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long cinemaId = objectMapper.readTree(cinemaJson).get("id").asLong();

        ScreenRequest screenRequest = new ScreenRequest("IMAX Hall 1", 5, 8, cinemaId); // 5r x 8s

        mockMvc.perform(post("/api/cinemas/screens")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(screenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(5))
                .andExpect(jsonPath("$.seatsPerRow").value(8));

        long seatCount = seatRepository.count();
        assertEquals(40, seatCount); // 5 rows * 8 seats
    }
}