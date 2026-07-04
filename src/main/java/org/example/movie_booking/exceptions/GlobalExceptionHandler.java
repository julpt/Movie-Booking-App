package org.example.movie_booking.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            UserNotFoundException.class,
            CinemaNotFoundException.class,
            MovieNotFoundException.class,
            ScreeningNotFoundException.class,
            BookingNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(Exception ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


    @ExceptionHandler({
            UsernameAlreadyUsedException.class,
            EmailAlreadyUsedException.class,
            SeatAlreadyBookedException.class
    })
    public ResponseEntity<Object> handleConflict(Exception ex) {
        log.error("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


    @ExceptionHandler({
            InvalidCredentialsException.class,
            SeatNotInScreenException.class,
            ScreeningNotInFutureException.class,
            InvalidBookingStatusException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        log.error("Bad Request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }



}
