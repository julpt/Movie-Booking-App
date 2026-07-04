package org.example.movie_booking.mapper;

import org.example.movie_booking.model.dto.PaymentRequest;
import org.example.movie_booking.model.dto.PaymentResponse;
import org.example.movie_booking.model.entities.Booking;
import org.example.movie_booking.model.entities.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request, Booking booking) {
        return Payment.builder()
                .booking(booking)
                .amount(request.amount())
                .method(request.method())
                .paidAt(LocalDateTime.now())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getPaidAt(),
                payment.getBooking().getId()
        );
    }
}