package org.example.movie_booking.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.movie_booking.model.entities.MovieGenre;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer durationMinutes,

        @NotNull(message = "Genre is required")
        MovieGenre genre,

        String rating,

        LocalDate releaseDate
) {}
