package org.example.movie_booking.service;

import org.example.movie_booking.exceptions.BookingNotFoundException;
import org.example.movie_booking.exceptions.PaymentNotFoundException;
import org.example.movie_booking.mapper.PaymentMapper;
import org.example.movie_booking.model.dto.PaymentRequest;
import org.example.movie_booking.model.dto.PaymentResponse;
import org.example.movie_booking.model.entities.Booking;
import org.example.movie_booking.model.entities.Payment;
import org.example.movie_booking.model.entities.PaymentMethod;
import org.example.movie_booking.repository.BookingRepository;
import org.example.movie_booking.repository.PaymentRepository;
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
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void whenBookingExists_createPayment_savesAndReturnsResponse() {
        Booking booking = Booking.builder().id(1L).build();
        PaymentRequest request = new PaymentRequest(1L, 20.0, PaymentMethod.CARD);
        Payment payment = Payment.builder().booking(booking).amount(20.0).method(PaymentMethod.CARD).build();
        Payment saved = Payment.builder().id(1L).booking(booking).amount(20.0).method(PaymentMethod.CARD).build();
        PaymentResponse response = new PaymentResponse(1L, 20.0, PaymentMethod.CARD, LocalDateTime.now(), 1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentMapper.toEntity(request, booking)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(saved);
        when(paymentMapper.toResponse(saved)).thenReturn(response);

        PaymentResponse result = paymentService.createPayment(request);

        assertNotNull(result);
        assertEquals(20.0, result.amount());
        verify(paymentRepository).save(payment);
    }

    @Test
    void whenBookingNotFound_createPayment_throwsException() {
        PaymentRequest request = new PaymentRequest(99L, 20.0, PaymentMethod.CARD);
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> paymentService.createPayment(request));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void getAllPayments_returnsMappedList() {
        Payment p1 = Payment.builder().id(1L).amount(20.0).build();
        Payment p2 = Payment.builder().id(2L).amount(30.0).build();

        when(paymentRepository.findAll()).thenReturn(List.of(p1, p2));
        when(paymentMapper.toResponse(p1)).thenReturn(new PaymentResponse(1L, 20.0, PaymentMethod.CARD, LocalDateTime.now(), 1L));
        when(paymentMapper.toResponse(p2)).thenReturn(new PaymentResponse(2L, 30.0, PaymentMethod.CASH, LocalDateTime.now(), 2L));

        List<PaymentResponse> result = paymentService.getAllPayments();

        assertEquals(2, result.size());
        verify(paymentRepository).findAll();
    }

    @Test
    void getPaymentById_whenExists_returnsResponse() {
        Payment payment = Payment.builder().id(1L).amount(20.0).build();
        PaymentResponse response = new PaymentResponse(1L, 20.0, PaymentMethod.CARD, LocalDateTime.now(), 1L);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.getPaymentById(1L);

        assertNotNull(result);
        verify(paymentRepository).findById(1L);
    }

    @Test
    void getPaymentById_whenNotFound_throwsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }

    @Test
    void updatePayment_whenExists_updatesFieldsAndReturns() {
        Payment payment = Payment.builder().id(1L).amount(20.0).method(PaymentMethod.CARD).build();
        Payment updated = Payment.builder().id(1L).amount(25.0).method(PaymentMethod.CASH).build();
        PaymentRequest request = new PaymentRequest(1L, 25.0, PaymentMethod.CASH);
        PaymentResponse response = new PaymentResponse(1L, 25.0, PaymentMethod.CASH, LocalDateTime.now(), 1L);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(updated);
        when(paymentMapper.toResponse(updated)).thenReturn(response);

        PaymentResponse result = paymentService.updatePayment(1L, request);

        assertEquals(25.0, result.amount());
        assertEquals(PaymentMethod.CASH, result.method());
    }

    @Test
    void deletePayment_whenExists_deletesSuccessfully() {
        Payment payment = Payment.builder().id(1L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.deletePayment(1L);

        verify(paymentRepository).delete(payment);
    }

    @Test
    void deletePayment_whenNotFound_throwsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PaymentNotFoundException.class, () -> paymentService.deletePayment(99L));
    }
}