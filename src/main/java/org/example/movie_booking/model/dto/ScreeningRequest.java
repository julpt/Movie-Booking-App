package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ScreeningRequest(
        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @Positive(message = "Price must be positive")
        Double price,

        @NotNull(message = "Movie ID is required")
        Long movieId,

        @NotNull(message = "Screen ID is required")
        Long screenId
) {}