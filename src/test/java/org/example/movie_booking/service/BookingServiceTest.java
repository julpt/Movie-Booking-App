package org.example.movie_booking.service;

import org.example.movie_booking.exceptions.ScreeningNotFoundException;
import org.example.movie_booking.exceptions.SeatAlreadyBookedException;
import org.example.movie_booking.exceptions.SeatNotInScreenException;
import org.example.movie_booking.exceptions.UserNotFoundException;
import org.example.movie_booking.mapper.BookingMapper;
import org.example.movie_booking.model.dto.BookingRequest;
import org.example.movie_booking.model.dto.BookingResponse;
import org.example.movie_booking.model.entities.*;
import org.example.movie_booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private BookedSeatRepository bookedSeatRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void whenCreateBooking_valid_createsBookingAndReturnsResponse() {

        // Arrange
        Long userId = 1L;
        Long screeningId = 10L;
        String movieTitle = "Inglourious Basterds";
        String cinemaName = "Cinema City";
        String screenName = "IMAX";

        User user = User.builder().id(userId).build();
        Screen screen = Screen.builder().id(100L).name("IMAX").build();
        LocalDateTime startTime = LocalDateTime.of(2027, 1, 1, 1, 1);

        BookingRequest request = new BookingRequest(
                userId,
                screeningId,
                List.of(100L, 101L)
        );

        Screening screening = Screening.builder()
                .id(screeningId)
                .price(10.0)
                .screen(screen)
                .startTime(startTime)
                .build();

        Seat seat1 = Seat.builder().id(100L).rowNumber(1).seatNumber(1).screen(screen).build();
        Seat seat2 = Seat.builder().id(101L).rowNumber(1).seatNumber(2).screen(screen).build();
        List<Seat> seats = List.of(seat1, seat2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        when(seatRepository.findAllById(request.seatIds())).thenReturn(seats);
        when(bookedSeatRepository.existsByScreeningIdAndSeatId(any(), any())).thenReturn(false);

        Booking savedBooking = Booking.builder()
                .id(50L)
                .user(user)
                .screening(screening)
                .status(BookingStatus.CONFIRMED)
                .totalPrice(20.0)
                .build();

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response  = new BookingResponse(
                50L,
                movieTitle,
                screening.getStartTime(),
                cinemaName,
                screenName,
                List.of("R1-1", "R1-2"),
                20.0,
                BookingStatus.CONFIRMED,
                LocalDateTime.now()
        );

        when(bookingMapper.toResponse(savedBooking))
                .thenReturn(response);

        // Act
        BookingResponse result = bookingService.createBooking(request);

        // Assert
        assertNotNull(result);
        assertEquals(response, result);

        verify(userRepository).findById(userId);
        verify(screeningRepository).findById(screeningId);
        verify(seatRepository).findAllById(request.seatIds());
        verify(bookingRepository).save(any(Booking.class));
        verify(bookingMapper).toResponse(savedBooking);

    }

    @Test
    void createBooking_whenUserNotFound_throwsUserNotFoundException() {
        BookingRequest request = new BookingRequest(1L, 10L, List.of(1L));

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenScreeningNotFound_throwsScreeningNotFoundException() {
        BookingRequest request = new BookingRequest(1L, 10L, List.of(1L));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User()));
        when(screeningRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ScreeningNotFoundException.class,
                () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
    }


    @Test
    void createBooking_whenSeatNotInScreen_throwsSeatNotInScreenException() {
        BookingRequest request = new BookingRequest(1L, 10L, List.of(1L));

        User user = new User();
        Screen screen1 = new Screen();
        screen1.setId(1L);

        Screen screen2 = new Screen();
        screen2.setId(2L);

        Screening screening = new Screening();
        screening.setId(10L);
        screening.setScreen(screen1);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setRowNumber(1);
        seat.setSeatNumber(1);
        seat.setScreen(screen2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));
        when(seatRepository.findAllById(any())).thenReturn(List.of(seat));

        assertThrows(SeatNotInScreenException.class,
                () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenSeatAlreadyBooked_throwsSeatAlreadyBookedException() {
        BookingRequest request = new BookingRequest(1L, 10L, List.of(1L));

        User user = new User();
        Screen screen = new Screen();
        screen.setId(1L);

        Screening screening = new Screening();
        screening.setId(10L);
        screening.setScreen(screen);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setRowNumber(1);
        seat.setSeatNumber(1);
        seat.setScreen(screen);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));
        when(seatRepository.findAllById(any())).thenReturn(List.of(seat));
        when(bookedSeatRepository.existsByScreeningIdAndSeatId(10L, 1L))
                .thenReturn(true);

        assertThrows(SeatAlreadyBookedException.class,
                () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
    }


    @Test
    void getUserHistory_returnsMappedBookingResponses() {
        Long userId = 5L;

        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        LocalDateTime startTime = LocalDateTime.of(2027, 1, 1, 1, 1);
        LocalDateTime bookTime = LocalDateTime.of(2026, 1, 1, 1, 1);

        BookingResponse resp1 = new BookingResponse(1L,
                "title",
                startTime,
                "cinemaName",
                "screenName",
                List.of("R1-1", "R1-2"),
                20.0,
                BookingStatus.CONFIRMED,
                bookTime);
        BookingResponse resp2 = new BookingResponse(2L,
                "title",
                startTime,
                "cinemaName",
                "screenName",
                List.of("R4-12", "R4-13"),
                20.0,
                BookingStatus.CONFIRMED,
                bookTime);

        when(bookingRepository.findBookingsWithDetails(userId))
                .thenReturn(List.of(booking1, booking2));

        when(bookingMapper.toResponse(booking1)).thenReturn(resp1);
        when(bookingMapper.toResponse(booking2)).thenReturn(resp2);

        List<BookingResponse> result = bookingService.getUserHistory(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(resp1));
        assertTrue(result.contains(resp2));
    }



}
