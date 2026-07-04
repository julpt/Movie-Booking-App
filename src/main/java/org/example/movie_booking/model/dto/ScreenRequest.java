package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScreenRequest(
        @NotBlank(message = "Screen name is required")
        String name,

        @Min(value = 1, message = "Must have at least 1 row")
        Integer totalRows,

        @Min(value = 1, message = "Must have at least 1 seat per row")
        Integer seatsPerRow,

        @NotNull(message = "Cinema ID is required")
        Long cinemaId
) {}
