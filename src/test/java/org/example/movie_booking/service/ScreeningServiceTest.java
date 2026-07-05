package org.example.movie_booking.service;

import org.example.movie_booking.exceptions.MovieNotFoundException;
import org.example.movie_booking.exceptions.ScreeningNotInFutureException;
import org.example.movie_booking.mapper.ScreeningMapper;
import org.example.movie_booking.model.dto.BookedSeatResponse;
import org.example.movie_booking.model.dto.ScreeningRequest;
import org.example.movie_booking.model.dto.ScreeningResponse;
import org.example.movie_booking.model.entities.*;
import org.example.movie_booking.repository.BookedSeatRepository;
import org.example.movie_booking.repository.MovieRepository;
import org.example.movie_booking.repository.ScreenRepository;
import org.example.movie_booking.repository.ScreeningRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScreeningServiceTest {

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScreenRepository screenRepository;

    @Mock
    private ScreeningMapper screeningMapper;

    @Mock
    private BookedSeatRepository bookedSeatRepository;

    @InjectMocks
    private ScreeningService screeningService;

    @Test
    void whenValidRequest_addScreening_createsAndReturnsResponse() {
        Long movieId = 1L;
        Long screenId = 10L;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);

        Movie movie = Movie.builder().id(movieId).title("Dune").durationMinutes(155).build();
        Cinema cinema = Cinema.builder().id(5L).name("Cinema X").city("Bucuresti").address("Strada Mare 123").build();
        Screen screen = Screen.builder().id(screenId).name("2D 1").cinema(cinema).build();

        ScreeningRequest request = new ScreeningRequest(startTime, 20.0, movieId, screenId);

        Screening saved = Screening.builder().id(100L).startTime(startTime).movie(movie).screen(screen).price(20.0).build();
        ScreeningResponse response = new ScreeningResponse(100L, startTime, startTime.plusMinutes(155), 20.0, "Dune", "Cinema X", "2D 1");

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(screenRepository.findById(screenId)).thenReturn(Optional.of(screen));
        when(screeningRepository.existsOverlapping(eq(screenId), eq(startTime), any())).thenReturn(false);
        when(screeningRepository.save(any(Screening.class))).thenReturn(saved);
        when(screeningMapper.toResponse(saved)).thenReturn(response);

        ScreeningResponse result = screeningService.addScreening(request);

        assertNotNull(result);
        assertEquals(100L, result.id());
        verify(screeningRepository).save(any(Screening.class));
    }

    @Test
    void whenMovieNotFound_addScreening_throwsException() {
        ScreeningRequest request = new ScreeningRequest(LocalDateTime.now().plusDays(1), 20.0, 99L, 10L);
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> screeningService.addScreening(request));
        verify(screeningRepository, never()).save(any());
    }

    @Test
    void whenScreenNotFound_addScreening_throwsRuntimeException() {
        Movie movie = Movie.builder().id(1L).durationMinutes(120).build();
        ScreeningRequest request = new ScreeningRequest(LocalDateTime.now().plusDays(1), 20.0, 99L, 10L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(screenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> screeningService.addScreening(request));
        verify(screeningRepository, never()).save(any());
    }

    @Test
    void whenStartTimeInPast_addScreening_throwsScreeningNotInFutureException() {
        Movie movie = Movie.builder().id(1L).durationMinutes(120).build();
        Screen screen = Screen.builder().id(10L).build();
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        ScreeningRequest request = new ScreeningRequest(pastTime, 20.0, 1L, 10L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(screenRepository.findById(10L)).thenReturn(Optional.of(screen));

        assertThrows(ScreeningNotInFutureException.class, () -> screeningService.addScreening(request));
        verify(screeningRepository, never()).save(any());
    }

    @Test
    void whenOverlappingScreening_addScreening_throwsRuntimeException() {
        Movie movie = Movie.builder().id(1L).durationMinutes(120).build();
        Screen screen = Screen.builder().id(10L).build();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        ScreeningRequest request = new ScreeningRequest(startTime, 20.0, 1L, 10L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(screenRepository.findById(10L)).thenReturn(Optional.of(screen));
        when(screeningRepository.existsOverlapping(eq(10L), eq(startTime), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> screeningService.addScreening(request));
        verify(screeningRepository, never()).save(any());
    }

    @Test
    void whenMovieExists_getScreeningsByMovie_returnsMappedList() {
        Long movieId = 1L;
        Movie movie = Movie.builder().id(movieId).build();
        Screening screening1 = Screening.builder().id(1L).build();
        Screening screening2 = Screening.builder().id(2L).build();


        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(screeningRepository.findByMovieId(movieId)).thenReturn(List.of(screening1, screening2));
        when(screeningMapper.toResponse(screening1)).thenReturn(new ScreeningResponse(1L, null, null, 20.0, "Dune", "Cinema X", "Sala 1"));
        when(screeningMapper.toResponse(screening2)).thenReturn(new ScreeningResponse(2L, null, null, 20.0, "Dune", "Cinema X", "Sala 2"));

        List<ScreeningResponse> result = screeningService.getScreeningsByMovie(movieId);

        assertEquals(2, result.size());
        verify(screeningRepository).findByMovieId(movieId);
    }

    @Test
    void whenMovieNotFound_getScreeningsByMovie_throwsException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(MovieNotFoundException.class, () -> screeningService.getScreeningsByMovie(99L));
    }

    @Test
    void whenScreeningExists_getBookedSeats_returnsMappedList() {
        Long screeningId = 1L;
        Seat seat = Seat.builder().rowNumber(2).seatNumber(5).build();
        BookedSeat bookedSeat = BookedSeat.builder().seat(seat).build();

        when(screeningRepository.existsById(screeningId)).thenReturn(true);
        when(bookedSeatRepository.findByScreeningId(screeningId)).thenReturn(List.of(bookedSeat));

        List<BookedSeatResponse> result = screeningService.getBookedSeats(screeningId);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).row());
        assertEquals(5, result.get(0).number());
    }

    @Test
    void whenScreeningNotFound_getBookedSeats_throwsRuntimeException() {
        when(screeningRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> screeningService.getBookedSeats(99L));
    }
}