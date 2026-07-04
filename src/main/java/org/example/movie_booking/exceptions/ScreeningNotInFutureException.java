package org.example.movie_booking.exceptions;

public class ScreeningNotInFutureException extends RuntimeException{
    public ScreeningNotInFutureException() {
        super("Screening must be in the future");
    }
}
