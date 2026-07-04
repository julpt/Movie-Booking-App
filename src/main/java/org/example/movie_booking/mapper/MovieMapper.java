package org.example.movie_booking.mapper;

import org.example.movie_booking.model.dto.MovieRequest;
import org.example.movie_booking.model.dto.MovieResponse;
import org.example.movie_booking.model.entities.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequest request) {
        return Movie.builder()
                .title(request.title())
                .description(request.description())
                .genre(request.genre())
                .rating(request.rating())
                .durationMinutes(request.durationMinutes())
                .releaseDate(request.releaseDate())
                .build();
    }

    public MovieResponse toResponse(Movie movie) {
        return new MovieResponse(movie.getId(),movie.getTitle(),movie.getDescription(),movie.getDurationMinutes(),movie.getGenre(),movie.getRating(),movie.getReleaseDate());
    }
}
