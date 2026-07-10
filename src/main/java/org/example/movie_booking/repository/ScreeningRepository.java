package org.example.movie_booking.repository;

import org.example.movie_booking.model.entities.Screening;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByMovieId(Long movieId);


    List<Screening> findByScreenId(Long screenId);

    boolean existsByScreenId(Long screenId);

    @Query("""
    SELECT COUNT(s) > 0 FROM Screening s
    WHERE s.screen.id = :screenId
    AND (
        (:newStart < s.endTime) AND (:newEnd > s.startTime)
    )
    """)
    boolean existsOverlapping(
            @Param("screenId") Long screenId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );

    Page<Screening> findByMovieIdAndStartTimeAfter(Long movieId, LocalDateTime startTime, Pageable pageable);
}