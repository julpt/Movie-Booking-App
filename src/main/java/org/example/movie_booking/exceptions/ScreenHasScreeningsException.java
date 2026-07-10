package org.example.movie_booking.exceptions;

public class ScreenHasScreeningsException extends RuntimeException {
    public ScreenHasScreeningsException(Long screenId) {
        super("Cannot delete screen with id " + screenId + ": it has existing screenings");
    }
}
