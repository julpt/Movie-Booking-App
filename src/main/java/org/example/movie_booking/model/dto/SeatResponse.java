package org.example.movie_booking.model.dto;

public record SeatResponse(
        Long id,
        Integer rowNumber,
        Integer seatNumber
) {}
