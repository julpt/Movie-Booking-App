package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.BookingRequest;
import org.example.movie_booking.model.dto.BookingStatusUpdateRequest;
import org.example.movie_booking.model.dto.SeatStatusResponse;
import org.example.movie_booking.model.entities.BookingStatus;
import org.example.movie_booking.model.entities.PaymentMethod;
import org.example.movie_booking.model.entities.Seat;
import org.example.movie_booking.model.entities.User;
import org.example.movie_booking.repository.UserRepository;
import org.example.movie_booking.service.BookingService;
import org.example.movie_booking.service.ScreeningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookingViewController {

    private final BookingService bookingService;
    private final ScreeningService screeningService;
    private final UserRepository userRepository;

    @GetMapping("/screenings/{id}/book")
    public String showBookingForm(@PathVariable Long id, Model model) {
        List<SeatStatusResponse> seats = screeningService.getSeatsWithStatus(id);

        // afisare grid
        Map<Integer, List<SeatStatusResponse>> seatsByRow = seats.stream()
                .collect(Collectors.groupingBy(SeatStatusResponse::rowNumber, TreeMap::new, Collectors.toList()));

        model.addAttribute("screeningId", id);
        model.addAttribute("seatsByRow", seatsByRow);
        return "bookings/form";
    }

    @PostMapping("/screenings/{id}/book")
    public String createBooking(@PathVariable Long id,
                                @RequestParam List<Long> seatIds,
                                @RequestParam PaymentMethod paymentMethod,
                                Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookingRequest request = new BookingRequest(user.getId(), id, seatIds, paymentMethod);
        bookingService.createBooking(request);

        return "redirect:/profile";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.updateBookingStatus(id, new BookingStatusUpdateRequest(BookingStatus.CANCELLED));
        return "redirect:/profile";
    }
}