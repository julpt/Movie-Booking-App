package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.ScreeningResponse;
import org.example.movie_booking.service.MovieService;
import org.example.movie_booking.service.ScreeningService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ScreeningViewController {

    private final ScreeningService screeningService;
    private final MovieService movieService;

    @GetMapping("/movies/{movieId}/screenings")
    public String screeningsForMovie(@PathVariable Long movieId, Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "startTime") String sortBy,
                                     @RequestParam(defaultValue = "asc") String direction) {

        Page<ScreeningResponse> screeningPage = screeningService.getUpcomingScreeningsForMovie(movieId, page, size, sortBy, direction);
        model.addAttribute("screeningPage", screeningPage);
        model.addAttribute("movie", movieService.getMovieById(movieId));
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentDirection", direction);
        return "screenings/list";
    }
}