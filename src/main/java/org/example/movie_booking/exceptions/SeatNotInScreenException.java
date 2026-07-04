package org.example.movie_booking.exceptions;

public class SeatNotInScreenException extends RuntimeException {
    public SeatNotInScreenException(int rowNumber, int seatNumber) {
        super("Seat at row " + rowNumber + " number " + seatNumber + " not found in selected room.");
    }
}
