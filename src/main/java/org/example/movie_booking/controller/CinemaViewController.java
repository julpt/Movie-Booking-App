package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.CinemaRequest;
import org.example.movie_booking.model.dto.CinemaResponse;
import org.example.movie_booking.service.CinemaService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cinemas")
@RequiredArgsConstructor
public class CinemaViewController {

    private final CinemaService cinemaService;

    @GetMapping
    public String listCinemas(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(defaultValue = "name") String sortBy,
                              @RequestParam(defaultValue = "asc") String direction) {
        Page<CinemaResponse> cinemaPage = cinemaService.getCinemasPaged(page, size, sortBy, direction);
        model.addAttribute("cinemaPage", cinemaPage);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentDirection", direction);
        return "cinemas/list";
    }

    @GetMapping("/new")
    public String newCinemaForm(Model model) {
        model.addAttribute("cinema", new CinemaRequest(null, null, null));
        return "cinemas/form";
    }

    @GetMapping("/edit/{id}")
    public String editCinemaForm(@PathVariable Long id, Model model) {
        CinemaResponse cinema = cinemaService.getCinemaById(id);
        model.addAttribute("cinema", new CinemaRequest(cinema.name(), cinema.city(), cinema.address()));
        model.addAttribute("cinemaId", id);
        return "cinemas/form";
    }

    @PostMapping
    public String saveCinema(@Validated @ModelAttribute("cinema") CinemaRequest cinema, BindingResult result) {
        if (result.hasErrors()) return "cinemas/form";
        cinemaService.createCinema(cinema);
        return "redirect:/cinemas";
    }

    @PostMapping("/{id}")
    public String updateCinema(@PathVariable Long id, @Validated @ModelAttribute("cinema") CinemaRequest cinema,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cinemaId", id);
            return "cinemas/form";
        }
        cinemaService.updateCinema(id, cinema);
        return "redirect:/cinemas";
    }

    @PostMapping("/delete/{id}")
    public String deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return "redirect:/cinemas";
    }
}