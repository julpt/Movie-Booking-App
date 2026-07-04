package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.NotNull;
import org.example.movie_booking.model.entities.BookingStatus;

public record BookingStatusUpdateRequest(
        @NotNull BookingStatus status
) {}