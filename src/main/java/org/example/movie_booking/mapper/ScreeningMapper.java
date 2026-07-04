package org.example.movie_booking.mapper;

import org.example.movie_booking.model.dto.ScreeningResponse;
import org.example.movie_booking.model.entities.Screening;
import org.springframework.stereotype.Component;

@Component
public class ScreeningMapper {

    public ScreeningResponse toResponse(Screening s) {
        return new ScreeningResponse(
                s.getId(), s.getStartTime(), s.getEndTime(), s.getPrice(),
                s.getMovie().getTitle(), s.getScreen().getCinema().getName(), s.getScreen().getName()
        );
    }
}
