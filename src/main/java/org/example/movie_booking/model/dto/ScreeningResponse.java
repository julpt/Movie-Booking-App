package org.example.movie_booking.model.dto;

import java.time.LocalDateTime;

public record ScreeningResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double price,
        String movieTitle,
        String cinemaName,
        String screenName
) {}
