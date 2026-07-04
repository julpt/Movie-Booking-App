package org.example.movie_booking.exceptions;

public class ScreeningNotFoundException extends RuntimeException {
    public ScreeningNotFoundException(Long screeningId) {
        super("Screening not found: " + screeningId);
    }
}
