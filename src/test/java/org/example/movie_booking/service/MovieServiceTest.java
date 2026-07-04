package org.example.movie_booking.service;

import org.example.movie_booking.mapper.MovieMapper;
import org.example.movie_booking.model.dto.MovieRequest;
import org.example.movie_booking.model.dto.MovieResponse;
import org.example.movie_booking.model.entities.Movie;
import org.example.movie_booking.model.entities.MovieGenre;
import org.example.movie_booking.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    @Test
    void whenMovieExists_findById_returnsMovieResponse(){
        // Arrange
        Movie movie = Movie.builder().id(1L).title("Inception").build();
        MovieResponse response = new MovieResponse(1L, "Inception", null, null, null, null, null);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.toResponse(movie)).thenReturn(response);

        // Act
        MovieResponse result = movieService.getMovieById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Inception", result.title());
        verify(movieRepository).findById(1L);
    }

    @Test
    void whenMoviesExist_getAllMovies_returnsListOfResponses() {
        // Arrange
        Movie movie1 = Movie.builder().id(1L).title("Inception").build();
        Movie movie2 = Movie.builder().id(2L).title("Interstellar").build();

        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));
        when(movieMapper.toResponse(movie1)).thenReturn(new MovieResponse(1L, "Inception", null, null, null, null, null));
        when(movieMapper.toResponse(movie2)).thenReturn(new MovieResponse(2L, "Interstellar", null, null, null, null, null));

        // Act
        List<MovieResponse> responses = movieService.getAllMovies();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Inception", responses.get(0).title());
        assertEquals("Interstellar", responses.get(1).title());
        verify(movieRepository).findAll();
    }

    @Test
    void whenAddMovie_createsAndReturnsMovie() {
        // Arrange
        MovieRequest request = new MovieRequest("Inception", "Dreams within dreams", 148, null, "PG-13", LocalDate.of(2010,7,16));
        Movie entity = Movie.builder().title("Inception").build();
        Movie saved = Movie.builder().id(1L).title("Inception").build();
        MovieResponse response = new MovieResponse(1L, "Inception", "Dreams within dreams", 148, null, "PG-13", LocalDate.of(2010,7,16));

        when(movieMapper.toEntity(request)).thenReturn(entity);
        when(movieRepository.save(entity)).thenReturn(saved);
        when(movieMapper.toResponse(saved)).thenReturn(response);

        // Act
        MovieResponse result = movieService.addMovie(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Inception", result.title());
        verify(movieRepository).save(entity);
    }

    @Test
    void whenUpdateMovie_existingMovie_updatesAndReturns() {
        // Arrange
        Long movieId = 1L;
        MovieRequest request = new MovieRequest("Inception Updated", "Desc", 150, null, "PG-13", LocalDate.of(2010,7,16));
        Movie movie = Movie.builder().id(movieId).title("Inception").build();
        Movie updated = Movie.builder().id(movieId).title("Inception Updated").build();
        MovieResponse response = new MovieResponse(movieId, "Inception Updated", "Desc", 150, null, "PG-13", LocalDate.of(2010,7,16));

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(updated);
        when(movieMapper.toResponse(updated)).thenReturn(response);

        // Act
        MovieResponse result = movieService.updateMovie(movieId, request);

        // Assert
        assertNotNull(result);
        assertEquals("Inception Updated", result.title());
        verify(movieRepository).findById(movieId);
        verify(movieRepository).save(movie);
    }

    @Test
    void whenDeleteMovie_existingMovie_deletes() {
        // Arrange
        Long movieId = 1L;
        Movie movie = Movie.builder().id(movieId).title("Inception").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        // Act
        movieService.deleteMovie(movieId);

        // Assert
        verify(movieRepository).delete(movie);
    }

    @Test
    void whenSearchMoviesWithQuery_returnsMatchingMovies() {
        // Arrange
        String query = "Avengers";
        Movie movie1 = new Movie(1L, "Avengers: Endgame", "Desc", 180, MovieGenre.ACTION, "PG-13", null, null);
        Movie movie2 = new Movie(2L, "Avengers: Infinity War", "Desc", 150, MovieGenre.ACTION, "PG-13", null, null);

        when(movieRepository.findByTitleContainingIgnoreCase(query))
                .thenReturn(List.of(movie1, movie2));

        when(movieMapper.toResponse(movie1)).thenReturn(new MovieResponse(1L, "Avengers: Endgame", "Desc", 180, MovieGenre.ACTION, "PG-13", null));
        when(movieMapper.toResponse(movie2)).thenReturn(new MovieResponse(2L, "Avengers: Infinity War", "Desc", 150, MovieGenre.ACTION, "PG-13", null));

        // Act
        List<MovieResponse> responses = movieService.searchMovies(query);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Avengers: Endgame", responses.get(0).title());
        assertEquals("Avengers: Infinity War", responses.get(1).title());

        verify(movieRepository).findByTitleContainingIgnoreCase(query);
        verify(movieMapper).toResponse(movie1);
        verify(movieMapper).toResponse(movie2);
    }


}
