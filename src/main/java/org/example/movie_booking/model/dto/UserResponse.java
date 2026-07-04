package org.example.movie_booking.model.dto;

public record UserResponse(
        Long id,
        String name,
        String username,
        String email
) {}
