package org.example.movie_booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.movie_booking.model.dto.BookingRequest;
import org.example.movie_booking.model.dto.BookingResponse;
import org.example.movie_booking.model.dto.BookingStatusUpdateRequest;
import org.example.movie_booking.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/users/{userId}")
    public List<BookingResponse> getUserHistory(@PathVariable Long userId) {
        return bookingService.getUserHistory(userId);
    }

    @PatchMapping("/{id}/status")
    public BookingResponse updateStatus(@PathVariable Long id, @RequestBody @Valid BookingStatusUpdateRequest request) {
        return bookingService.updateBookingStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}