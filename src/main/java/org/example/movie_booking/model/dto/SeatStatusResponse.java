package org.example.movie_booking.model.dto;

public record SeatStatusResponse(
        Long id,
        Integer rowNumber,
        Integer seatNumber,
        boolean available
) {}