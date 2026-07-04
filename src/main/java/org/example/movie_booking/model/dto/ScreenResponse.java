package org.example.movie_booking.model.dto;

public record ScreenResponse(
        Long id,
        String name,
        Integer totalRows,
        Integer seatsPerRow,
        String cinemaName
) {}
