package com.cine.demo.repository;

import com.cine.demo.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    List<Screening> findByMovieId(Long movieId);
    List<Screening> findByTheaterId(Long theaterId);
    List<Screening> findByStartDatetimeAfter(LocalDateTime datetime);

    @Query("SELECT s FROM Screening s WHERE s.movie.id = :movieId AND s.startDatetime > :now ORDER BY s.startDatetime ASC")
    List<Screening> findUpcomingByMovie(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie JOIN FETCH s.theater")
    List<Screening> findAllWithMovieAndTheater();

    long countByStartDatetimeAfter(LocalDateTime datetime);
}
