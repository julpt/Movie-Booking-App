package org.example.movie_booking.model.dto;

import org.example.movie_booking.model.entities.PaymentMethod;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Double amount,
        PaymentMethod method,
        LocalDateTime paidAt,
        Long bookingId
) {}