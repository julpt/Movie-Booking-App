package org.example.movie_booking.exceptions;

public class MovieNotFoundException extends RuntimeException {
  public MovieNotFoundException(Long movieId) {
    super("Movie with id " + movieId + " not found");
  }
}
