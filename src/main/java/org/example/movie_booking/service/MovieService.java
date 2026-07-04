package org.example.movie_booking.service;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.MovieNotFoundException;
import org.example.movie_booking.mapper.MovieMapper;
import org.example.movie_booking.model.dto.MovieRequest;
import org.example.movie_booking.model.dto.MovieResponse;
import org.example.movie_booking.model.entities.Movie;
import org.example.movie_booking.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    public MovieResponse addMovie(MovieRequest request) {
        log.info("Adding movie with title: {}", request.title());   // LOGGING
        Movie movie = movieMapper.toEntity(request);
        Movie savedMovie = movieRepository.save(movie);
        log.debug("Movie id: {}", savedMovie.getId());
        return movieMapper.toResponse(savedMovie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream().map(movieMapper::toResponse).collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Long movieId) {
        log.debug("Getting movie with id: {}", movieId); //LOGGING
        Movie movie = movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException(movieId));
        return movieMapper.toResponse(movie);
    }

    public List<MovieResponse> searchMovies(String query) {
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(query);
        log.debug("Searching movies with query: {}", query); //LOGGING
        return movies.stream().map(movieMapper::toResponse).collect(Collectors.toList());
    }

    public MovieResponse updateMovie(Long movieId, MovieRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));



        // update fields
        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationMinutes(request.durationMinutes());
        movie.setGenre(request.genre());
        movie.setRating(request.rating());
        movie.setReleaseDate(request.releaseDate());

        Movie updated = movieRepository.save(movie);
        log.info("Updating movie with id: {}", movieId); //LOGGING
        return movieMapper.toResponse(updated);
    }

    public void deleteMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        movieRepository.delete(movie);
        log.info("Deleted movie with id: {}", movieId); //LOGGING
    }

    public Page<MovieResponse> getMoviesPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return movieRepository.findAll(pageable).map(movieMapper::toResponse);
    }


}
