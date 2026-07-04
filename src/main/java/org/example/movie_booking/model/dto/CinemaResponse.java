package org.example.movie_booking.model.dto;

public record CinemaResponse(
        Long id,
        String name,
        String city,
        String address
) {}
