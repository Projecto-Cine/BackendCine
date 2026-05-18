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
    List<Screening> findByStartTimeAfter(LocalDateTime dateTime);

    @Query("SELECT s FROM Screening s WHERE s.movie.id = :movieId AND s.startTime > :now ORDER BY s.startTime ASC")
    List<Screening> findUpcomingByMovie(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie JOIN FETCH s.theater")
    List<Screening> findAllWithMovieAndTheater();

    @Query("SELECT s FROM Screening s WHERE s.startTime >= :start AND s.startTime < :end ORDER BY s.startTime ASC")
    List<Screening> findByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    long countByStartTimeAfter(LocalDateTime dateTime);

    @Query("SELECT COUNT(s) FROM Screening s WHERE YEAR(s.startTime) = :year")
    long countByYear(@Param("year") int year);

    @Query("SELECT COUNT(DISTINCT s.movie.id) FROM Screening s WHERE YEAR(s.startTime) = :year")
    long countDistinctMoviesByYear(@Param("year") int year);
}
