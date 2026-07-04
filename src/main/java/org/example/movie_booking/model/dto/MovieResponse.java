package org.example.movie_booking.model.dto;

import org.example.movie_booking.model.entities.MovieGenre;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        MovieGenre genre,
        String rating,
        LocalDate releaseDate
) {}