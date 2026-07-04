package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FavoriteViewController {

    private final UserService userService;

    @PostMapping("/movies/{id}/favorite")
    public String addFavorite(@PathVariable Long id, Principal principal) {
        userService.addFavoriteMovie(principal.getName(), id);
        return "redirect:/movies";
    }

    @PostMapping("/movies/{id}/unfavorite")
    public String removeFavorite(@PathVariable Long id, Principal principal) {
        userService.removeFavoriteMovie(principal.getName(), id);
        return "redirect:/movies";
    }
}