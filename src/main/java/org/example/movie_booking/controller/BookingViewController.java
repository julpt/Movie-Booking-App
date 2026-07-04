package org.example.movie_booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.BookingStatusUpdateRequest;
import org.example.movie_booking.model.entities.BookingStatus;
import org.example.movie_booking.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class BookingViewController {

    private final BookingService bookingService;

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.updateBookingStatus(id, new BookingStatusUpdateRequest(BookingStatus.CANCELLED));
        return "redirect:/profile";
    }
}