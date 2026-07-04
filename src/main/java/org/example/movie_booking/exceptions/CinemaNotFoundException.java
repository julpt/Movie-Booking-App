package org.example.movie_booking.exceptions;

public class CinemaNotFoundException extends RuntimeException {
    public CinemaNotFoundException(Long cinemaId) {

        super("Cinema not found: " + cinemaId);
    }
}
