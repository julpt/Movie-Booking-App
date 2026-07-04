package org.example.movie_booking.service;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.MovieNotFoundException;
import org.example.movie_booking.exceptions.ScreeningNotInFutureException;
import org.example.movie_booking.mapper.ScreeningMapper;
import org.example.movie_booking.model.dto.BookedSeatResponse;
import org.example.movie_booking.model.dto.ScreeningRequest;
import org.example.movie_booking.model.dto.ScreeningResponse;
import org.example.movie_booking.model.entities.Movie;
import org.example.movie_booking.model.entities.Screen;
import org.example.movie_booking.model.entities.Screening;
import org.example.movie_booking.repository.BookedSeatRepository;
import org.example.movie_booking.repository.MovieRepository;
import org.example.movie_booking.repository.ScreenRepository;
import org.example.movie_booking.repository.ScreeningRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final ScreeningMapper screeningMapper;
    private final BookedSeatRepository bookedSeatRepository;

    public ScreeningResponse addScreening(ScreeningRequest request) {
        Movie movie = movieRepository.findById(request.movieId()).orElseThrow(() -> new MovieNotFoundException(request.movieId()));
        Screen screen = screenRepository.findById(request.screenId()).orElseThrow(() -> new RuntimeException("Screen not found"));
        // validate screening time
        if (request.startTime().isBefore(LocalDateTime.now())) {
            throw new ScreeningNotInFutureException();
        }

        LocalDateTime endTime = request.startTime().plusMinutes(movie.getDurationMinutes());

        // Check Overlap
        if (screeningRepository.existsOverlapping(request.screenId(), request.startTime(), endTime)) {
            throw new RuntimeException("Screen is already occupied at this time!");
        }

        Screening screening = Screening.builder().startTime(request.startTime())
                .endTime(endTime)
                .price(request.price())
                .movie(movie)
                .screen(screen)
                .build();
        Screening saved = screeningRepository.save(screening);
        return screeningMapper.toResponse(saved);
    }

    public List<ScreeningResponse> getScreeningsByMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        return screeningRepository.findByMovieId(movieId).stream().map(screeningMapper::toResponse).collect(Collectors.toList());
    }

    public List<BookedSeatResponse> getBookedSeats(Long screeningId) {
        if (!screeningRepository.existsById(screeningId)) {
            throw new RuntimeException("Screening not found");
    }
        return bookedSeatRepository.findByScreeningId(screeningId)
                .stream()
                .map(bookedSeat -> new BookedSeatResponse(
                        bookedSeat.getSeat().getRowNumber(),
                        bookedSeat.getSeat().getSeatNumber()
                ))
                .toList();
    }

    public Page<ScreeningResponse> getUpcomingScreeningsForMovie(Long movieId, int page, int size, String sortBy, String direction) {
        movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return screeningRepository.findByMovieIdAndStartTimeAfter(movieId, LocalDateTime.now(), pageable)
                .map(screeningMapper::toResponse);
    }


}
