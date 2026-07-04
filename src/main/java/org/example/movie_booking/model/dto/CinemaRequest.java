package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CinemaRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Address is required")
        String address
) {}
