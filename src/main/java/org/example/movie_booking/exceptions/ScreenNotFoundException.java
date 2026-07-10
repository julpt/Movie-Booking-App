package org.example.movie_booking.exceptions;

public class ScreenNotFoundException extends RuntimeException {
    public ScreenNotFoundException(Long id) {
        super("Screen not found with id: " + id);
    }
}
