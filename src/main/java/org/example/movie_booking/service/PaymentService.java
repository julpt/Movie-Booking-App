package org.example.movie_booking.service;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.BookingNotFoundException;
import org.example.movie_booking.exceptions.PaymentNotFoundException;
import org.example.movie_booking.mapper.PaymentMapper;
import org.example.movie_booking.model.dto.PaymentRequest;
import org.example.movie_booking.model.dto.PaymentResponse;
import org.example.movie_booking.model.entities.Booking;
import org.example.movie_booking.model.entities.Payment;
import org.example.movie_booking.repository.BookingRepository;
import org.example.movie_booking.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;

    public PaymentResponse createPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(request.bookingId()));

        Payment payment = paymentMapper.toEntity(request, booking);
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return paymentMapper.toResponse(payment);
    }

    public PaymentResponse updatePayment(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponse(updated);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        paymentRepository.delete(payment);
    }
}