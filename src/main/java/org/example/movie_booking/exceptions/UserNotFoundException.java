package org.example.movie_booking.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long userId) {
    super("User not found: " + userId);
  }
  public UserNotFoundException(String username) {
    super("User not found with username: " + username);
  }
}
