package org.example.movie_booking.service;

import org.example.movie_booking.exceptions.CinemaNotFoundException;
import org.example.movie_booking.model.dto.CinemaResponse;
import org.example.movie_booking.model.dto.ScreenResponse;
import org.example.movie_booking.model.entities.Cinema;
import org.example.movie_booking.model.entities.Screen;
import org.example.movie_booking.repository.CinemaRepository;
import org.example.movie_booking.repository.ScreenRepository;
import org.example.movie_booking.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ScreenRepository screenRepository;

    @InjectMocks
    private CinemaService cinemaService;

    @Test
    void getAllCinemas_returnsMappedList() {
        Cinema cinema1 = Cinema.builder().id(1L).name("Cinema City").city("Bucuresti").address("Str. B 1").build();
        Cinema cinema2 = Cinema.builder().id(2L).name("Grand Cinema").city("Cluj").address("Str. C 2").build();

        when(cinemaRepository.findAll()).thenReturn(List.of(cinema1, cinema2));

        List<CinemaResponse> result = cinemaService.getAllCinemas();

        assertEquals(2, result.size());
        assertEquals("Cinema City", result.get(0).name());
        verify(cinemaRepository).findAll();
    }

    @Test
    void whenCinemaExists_getCinemaById_returnsResponse() {
        Cinema cinema = Cinema.builder().id(1L).name("Cinema City").city("Bucuresti").address("Str. B 1").build();
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        CinemaResponse result = cinemaService.getCinemaById(1L);

        assertNotNull(result);
        assertEquals("Cinema City", result.name());
        verify(cinemaRepository).findById(1L);
    }

    @Test
    void whenCinemaNotFound_getCinemaById_throwsException() {
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CinemaNotFoundException.class, () -> cinemaService.getCinemaById(99L));
    }

    @Test
    void getCinemasPaged_returnsMappedPage() {
        Cinema cinema = Cinema.builder().id(1L).name("Cinema City").city("Bucuresti").address("Str. B 1").build();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Cinema> cinemaPage = new PageImpl<>(List.of(cinema), pageable, 1);

        when(cinemaRepository.findAll(any(Pageable.class))).thenReturn(cinemaPage);

        Page<CinemaResponse> result = cinemaService.getCinemasPaged(0, 5, "name", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals("Cinema City", result.getContent().get(0).name());
    }

    @Test
    void whenCinemaExists_updateCinema_updatesFieldsAndReturns() {
        Cinema cinema = Cinema.builder().id(1L).name("Nume Vechi").city("Oras Vechi").address("Adresa Veche").build();
        var request = new org.example.movie_booking.model.dto.CinemaRequest("Nume Nou", "Oras Nou", "Adresa Noua");

        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        when(cinemaRepository.save(cinema)).thenReturn(cinema);

        CinemaResponse result = cinemaService.updateCinema(1L, request);

        assertEquals("Nume Nou", result.name());
        assertEquals("Oras Nou", result.city());
        assertEquals("Adresa Noua", result.address());
        verify(cinemaRepository).save(cinema);
    }

    @Test
    void whenCinemaNotFound_updateCinema_throwsException() {
        var request = new org.example.movie_booking.model.dto.CinemaRequest("Nume Nou", "Oras Nou", "Adresa Noua");
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CinemaNotFoundException.class, () -> cinemaService.updateCinema(99L, request));
        verify(cinemaRepository, never()).save(any());
    }

    @Test
    void whenCinemaExists_deleteCinema_deletesSuccessfully() {
        Cinema cinema = Cinema.builder().id(1L).build();
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        cinemaService.deleteCinema(1L);

        verify(cinemaRepository).delete(cinema);
    }

    @Test
    void whenCinemaNotFound_deleteCinema_throwsException() {
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CinemaNotFoundException.class, () -> cinemaService.deleteCinema(99L));
        verify(cinemaRepository, never()).delete(any());
    }

    @Test
    void whenCinemaExists_getScreensByCinema_returnsMappedList() {
        Long cinemaId = 1L;
        Cinema cinema = Cinema.builder().id(cinemaId).name("Cinema City").build();
        Screen screen = Screen.builder().id(10L).name("Sala 1").totalRows(5).seatsPerRow(8).cinema(cinema).build();

        when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.of(cinema));
        when(screenRepository.findByCinemaId(cinemaId)).thenReturn(List.of(screen));

        List<ScreenResponse> result = cinemaService.getScreensByCinema(cinemaId);

        assertEquals(1, result.size());
        assertEquals("Sala 1", result.get(0).name());
        verify(screenRepository).findByCinemaId(cinemaId);
    }

    @Test
    void whenCinemaNotFound_getScreensByCinema_throwsException() {
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CinemaNotFoundException.class, () -> cinemaService.getScreensByCinema(99L));
        verify(screenRepository, never()).findByCinemaId(any());
    }
}