package org.example.movie_booking.service;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.CinemaNotFoundException;
import org.example.movie_booking.model.dto.CinemaRequest;
import org.example.movie_booking.model.dto.CinemaResponse;
import org.example.movie_booking.model.dto.ScreenRequest;
import org.example.movie_booking.model.dto.ScreenResponse;
import org.example.movie_booking.model.entities.Cinema;
import org.example.movie_booking.model.entities.Screen;
import org.example.movie_booking.model.entities.Seat;
import org.example.movie_booking.repository.CinemaRepository;
import org.example.movie_booking.repository.ScreenRepository;
import org.example.movie_booking.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaService {
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    private static final Logger log = LoggerFactory.getLogger(CinemaService.class);

    public CinemaResponse createCinema(CinemaRequest request) {
        Cinema cinema = Cinema.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .build();
        Cinema saved = cinemaRepository.save(cinema);
        log.info("Adding cinema with name: {}", request.name());   // LOGGING
        log.debug("Cinema id: {}", cinema.getId());   // LOGGING
        return toResponse(saved);
    }

    public List<CinemaResponse> getAllCinemas() {
        log.info("Fetching all cinemas");   // LOGGING
        return cinemaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CinemaResponse getCinemaById(Long id) {
        log.info("Fetching cinema with name: {}", id);   // LOGGING
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new CinemaNotFoundException(id));
        return toResponse(cinema);
    }
    public Page<CinemaResponse> getCinemasPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return cinemaRepository.findAll(pageable).map(this::toResponse);
    }

    public CinemaResponse updateCinema(Long id, CinemaRequest request) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new CinemaNotFoundException(id));
        cinema.setName(request.name());
        cinema.setCity(request.city());
        cinema.setAddress(request.address());
        Cinema updated = cinemaRepository.save(cinema);
        log.info("Updating cinema with id: {}", id);   // LOGGING
        return toResponse(updated);
    }

    public void deleteCinema(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new CinemaNotFoundException(id));

        log.info("Deleting cinema with id: {}", id);   // LOGGING
        cinemaRepository.delete(cinema);
    }

    public List<ScreenResponse> getScreensByCinema(Long cinemaId) {
        cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new CinemaNotFoundException(cinemaId));

        log.info("Fetching screens in cinema with id: {}", cinemaId);   // LOGGING

        return screenRepository.findByCinemaId(cinemaId).stream().map(screen -> new ScreenResponse(
                        screen.getId(),
                        screen.getName(),
                        screen.getTotalRows(),
                        screen.getSeatsPerRow(),
                        screen.getCinema().getName()
                ))
                .toList();
    }

    @Transactional
    public ScreenResponse createScreen(ScreenRequest request) {
        Cinema cinema = cinemaRepository.findById(request.cinemaId())
                .orElseThrow(() -> new CinemaNotFoundException(request.cinemaId()));

        Screen screen = Screen.builder()
                .name(request.name())
                .totalRows(request.totalRows())
                .seatsPerRow(request.seatsPerRow())
                .cinema(cinema)
                .build();

        Screen savedScreen = screenRepository.save(screen);

        for (int row = 1; row <= request.totalRows(); row++) {
            for (int num = 1; num <= request.seatsPerRow(); num++) {
                Seat seat = Seat.builder()
                        .screen(savedScreen)
                        .rowNumber(row)
                        .seatNumber(num)
                        .build();
                seatRepository.save(seat);
            }
        }


        log.info("Creating screen in cinema with name: {}", request.name());   // LOGGING
        log.debug("New screen id: {}", screen.getId());   // LOGGING

        return new ScreenResponse(
                savedScreen.getId(), savedScreen.getName(),
                savedScreen.getTotalRows(), savedScreen.getSeatsPerRow(),
                cinema.getName()
        );
    }

    private CinemaResponse toResponse(Cinema c) {
        return new CinemaResponse(c.getId(), c.getName(), c.getCity(), c.getAddress());
    }
}