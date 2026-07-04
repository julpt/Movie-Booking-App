package org.example.movie_booking.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String username) {
        super("Username is already in use: " + username);
    }
}
