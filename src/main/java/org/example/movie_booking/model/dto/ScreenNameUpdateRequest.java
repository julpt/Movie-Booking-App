package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ScreenNameUpdateRequest(
        @NotBlank(message = "Screen name is required")
        String name
) {}