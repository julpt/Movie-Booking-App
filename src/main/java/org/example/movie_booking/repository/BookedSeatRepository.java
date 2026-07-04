package org.example.movie_booking.repository;

import org.example.movie_booking.model.entities.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {
    // find which seats are taken for a screening
    List<BookedSeat> findByScreeningId(Long screeningId);
    boolean existsByScreeningIdAndSeatId(Long screeningId, Long seatId);
}