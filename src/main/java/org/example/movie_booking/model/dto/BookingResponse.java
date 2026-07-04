package org.example.movie_booking.model.dto;

import org.example.movie_booking.model.entities.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String movieTitle,
        LocalDateTime screeningStartTime,
        String cinemaName,
        String screenName,
        List<String> seats,
        Double totalPrice,
        BookingStatus status,
        LocalDateTime bookingTime
) {}
