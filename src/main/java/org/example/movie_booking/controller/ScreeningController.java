package org.example.movie_booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.BookedSeatResponse;
import org.example.movie_booking.model.dto.ScreeningRequest;
import org.example.movie_booking.model.dto.ScreeningResponse;
import org.example.movie_booking.service.ScreeningService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService screeningService;

    @PostMapping
    public ScreeningResponse addScreening(@RequestBody @Valid ScreeningRequest request) {
        return screeningService.addScreening(request);
    }

    @GetMapping("/movies/{movieId}")
    public List<ScreeningResponse> getScreenings(@PathVariable Long movieId) {
        return screeningService.getScreeningsByMovie(movieId);
    }

    @GetMapping("/{id}/seats")
    public List<BookedSeatResponse> getBookedSeats(@PathVariable Long id) {
        return screeningService.getBookedSeats(id);
    }
}
