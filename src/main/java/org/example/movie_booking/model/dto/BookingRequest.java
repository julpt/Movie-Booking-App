package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.movie_booking.model.entities.PaymentMethod;

import java.util.List;

public record BookingRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Screening ID is required")
        Long screeningId,

        @NotEmpty(message = "Must select at least one seat")
        List<Long> seatIds,

        @NotNull(message = "Payment metrod is required")
        PaymentMethod paymentMethod
) {}
