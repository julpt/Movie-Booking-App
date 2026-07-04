package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.MovieRequest;
import org.example.movie_booking.model.dto.MovieResponse;
import org.example.movie_booking.model.entities.MovieGenre;
import org.example.movie_booking.service.MovieService;
import org.example.movie_booking.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieViewController {

    private final MovieService movieService;
    private final UserService userService;

    // READ (list)
    @GetMapping
    public String listMovies(Model model, Principal principal,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             @RequestParam(defaultValue = "title") String sortBy,
                             @RequestParam(defaultValue = "asc") String direction) {
        Page<MovieResponse> moviePage = movieService.getMoviesPaged(page, size, sortBy, direction);
        model.addAttribute("moviePage", moviePage);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentDirection", direction);

        if (principal != null) {
            List<Long> favoriteIds = userService.getFavoriteMovies(principal.getName())
                    .stream().map(MovieResponse::id).collect(Collectors.toList());
            model.addAttribute("favoriteIds", favoriteIds);
        }

        return "movies/list";
    }

    // CREATE - show empty form
    @GetMapping("/new")
    public String newMovieForm(Model model) {
        model.addAttribute("movie", new MovieRequest(null, null, null, null, null, null));
        model.addAttribute("genres", MovieGenre.values());
        return "movies/form";
    }

    // UPDATE - show pre-filled form
    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model) {
        MovieResponse movie = movieService.getMovieById(id);
        MovieRequest formData = new MovieRequest(
                movie.title(), movie.description(), movie.durationMinutes(),
                movie.genre(), movie.rating(), movie.releaseDate()
        );
        model.addAttribute("movie", formData);
        model.addAttribute("movieId", id); // ca formul să știe dacă e edit
        model.addAttribute("genres", MovieGenre.values());
        return "movies/form";
    }

    // CREATE - handle submit
    @PostMapping
    public String saveMovie(@Validated @ModelAttribute("movie") MovieRequest movie,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("genres", MovieGenre.values());
            return "movies/form";
        }
        movieService.addMovie(movie);
        return "redirect:/movies";
    }

    // UPDATE - handle submit
    @PostMapping("/{id}")
    public String updateMovie(@PathVariable Long id,
                              @Validated @ModelAttribute("movie") MovieRequest movie,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("movieId", id);
            model.addAttribute("genres", MovieGenre.values());
            return "movies/form";
        }
        movieService.updateMovie(id, movie);
        return "redirect:/movies";
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return "redirect:/movies";
    }
}