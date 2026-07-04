package org.example.movie_booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.*;
import org.example.movie_booking.service.CinemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {
    private final CinemaService cinemaService;

    @PostMapping
    public CinemaResponse createCinema(@RequestBody @Valid CinemaRequest request) {
        return cinemaService.createCinema(request);
    }

    @GetMapping
    public List<CinemaResponse> getAllCinemas() {
        return cinemaService.getAllCinemas();
    }

    @PostMapping("/screens")
    public ScreenResponse createScreen(@RequestBody @Valid ScreenRequest request) {
        return cinemaService.createScreen(request);
    }


    @GetMapping("/{cinemaId}/screens")
    public List<ScreenResponse> getScreensByCinema(@PathVariable Long cinemaId) {
        return cinemaService.getScreensByCinema(cinemaId);
    }
}
