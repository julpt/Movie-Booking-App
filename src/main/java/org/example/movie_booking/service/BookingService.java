package org.example.movie_booking.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.movie_booking.exceptions.*;
import org.example.movie_booking.mapper.BookingMapper;
import org.example.movie_booking.model.dto.BookingRequest;
import org.example.movie_booking.model.dto.BookingResponse;
import org.example.movie_booking.model.dto.BookingStatusUpdateRequest;
import org.example.movie_booking.model.entities.*;
import org.example.movie_booking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final BookingMapper bookingMapper;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(()->new UserNotFoundException(request.userId()));
        Screening screening = screeningRepository.findById(request.screeningId()).orElseThrow(()->new ScreeningNotFoundException(request.screeningId()));

        List<Seat> seats = seatRepository.findAllById(request.seatIds());
        if (seats.size() != request.seatIds().size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }

        for (Seat seat : seats) {
            if (!seat.getScreen().getId().equals(screening.getScreen().getId())) {
                throw new SeatNotInScreenException(seat.getRowNumber(),seat.getSeatNumber());
            }
            if (bookedSeatRepository.existsByScreeningIdAndSeatId(screening.getId(), seat.getId())) {
                throw new SeatAlreadyBookedException(seat.getRowNumber(), seat.getSeatNumber());
            }
        }

        Booking booking = Booking.builder()
                .user(user)
                .screening(screening)
                .bookingTime(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .totalPrice(screening.getPrice() * seats.size())
                .bookedSeats(new ArrayList<>())
                .build();

        for (Seat seat : seats) {
            BookedSeat bookedSeat = BookedSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .screening(screening)
                    .build();
            booking.getBookedSeats().add(bookedSeat);
        }


        Booking savedBooking = bookingRepository.save(booking);

        log.info("Creating booking");   // LOGGING
        log.debug("Booking id: {} User id:{}", booking.getId(), user.getId());   // LOGGING
        return bookingMapper.toResponse(savedBooking);
    }

    public List<BookingResponse> getUserHistory(Long userId) {
        List<Booking> bookings = bookingRepository.findBookingsWithDetails(userId);


        log.info("Fetching booking history for user with id: {}", userId);   // LOGGING
        return bookings.stream().map(bookingMapper::toResponse).collect(Collectors.toList());
    }

    // ---- READ (all) ----
    public List<BookingResponse> getAllBookings() {
        log.debug("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ---- READ (by id) ----
    public BookingResponse getBookingById(Long id) {
        log.debug("Fetching booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Booking not found with id: {}", id);
                    return new BookingNotFoundException(id);
                });
        return bookingMapper.toResponse(booking);
    }

    // ---- UPDATE (status) ----
    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatusUpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getStatus() == BookingStatus.CANCELLED && request.status() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStatusException("Booking is already cancelled");
        }

        booking.setStatus(request.status());
        Booking updated = bookingRepository.save(booking);
        log.info("Booking {} status updated to {}", id, request.status());
        return bookingMapper.toResponse(updated);
    }

    // ---- DELETE ----
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        bookingRepository.delete(booking);
        log.info("Booking deleted with id: {}", id);
    }

    public Page<BookingResponse> getUserBookingsPaged(Long userId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookingRepository.findByUserId(userId, pageable).map(bookingMapper::toResponse);
    }


}
