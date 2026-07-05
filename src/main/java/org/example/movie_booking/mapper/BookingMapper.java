package org.example.movie_booking.mapper;

import org.example.movie_booking.model.dto.BookingResponse;
import org.example.movie_booking.model.entities.Booking;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public BookingResponse toResponse(Booking booking) {
        List<String> seatLabels = booking.getBookedSeats().stream()
                .map(bs -> "Row " + bs.getSeat().getRowNumber() + " - Seat " + bs.getSeat().getSeatNumber())
                .collect(Collectors.toList());

        return new BookingResponse(
                booking.getId(),
                booking.getScreening().getMovie().getTitle(),
                booking.getScreening().getStartTime(),
                booking.getScreening().getScreen().getCinema().getName(),
                booking.getScreening().getScreen().getName(),
                seatLabels,
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getBookingTime(),
                booking.getPayment() != null ? booking.getPayment().getMethod() : null
        );
    }
}
