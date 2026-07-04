package org.example.movie_booking.exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long bookingId) {
        super("Booking not found: " + bookingId);
    }
}
