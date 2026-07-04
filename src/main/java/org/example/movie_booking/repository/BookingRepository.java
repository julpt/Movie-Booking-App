package org.example.movie_booking.repository;

import jakarta.transaction.Transactional;
import org.example.movie_booking.model.entities.Booking;
import org.example.movie_booking.model.entities.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    // See details of all past bookings associated with one user
    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.screening s
        JOIN FETCH s.movie
        JOIN FETCH s.screen sc
        JOIN FETCH sc.cinema
        WHERE b.user.id = :userId
        ORDER BY b.bookingTime DESC
    """)
    List<Booking> findBookingsWithDetails(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :id")
    void updateStatus(@Param("id") Long bookingId, @Param("status") BookingStatus status);

    Page<Booking> findByUserId(Long userId, Pageable pageable);
}
