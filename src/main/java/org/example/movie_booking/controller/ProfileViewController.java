package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.BookingResponse;
import org.example.movie_booking.model.dto.UserResponse;
import org.example.movie_booking.service.BookingService;
import org.example.movie_booking.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileViewController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/profile")
    public String profile(Principal principal, Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          @RequestParam(defaultValue = "bookingTime") String sortBy,
                          @RequestParam(defaultValue = "desc") String direction) {

        UserResponse user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("favoriteMovies", userService.getFavoriteMovies(principal.getName()));

        Page<BookingResponse> bookingPage = bookingService.getUserBookingsPaged(
                user.id(), page, size, sortBy, direction);
        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentDirection", direction);

        return "profile";
    }
}