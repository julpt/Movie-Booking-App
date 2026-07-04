package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.NotNull;
import org.example.movie_booking.model.entities.PaymentMethod;

public record PaymentRequest(
        @NotNull Long bookingId,
        @NotNull Double amount,
        @NotNull PaymentMethod method
) {}