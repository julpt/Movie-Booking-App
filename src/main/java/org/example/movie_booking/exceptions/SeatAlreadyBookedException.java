package org.example.movie_booking.exceptions;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(int rowNumber, int seatNumber) {
        super("Seat at row " + rowNumber + " number " + seatNumber + " is already booked");
    }
}
