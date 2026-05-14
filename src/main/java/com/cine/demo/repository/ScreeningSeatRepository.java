package com.cine.demo.repository;

import com.cine.demo.model.ScreeningSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningSeatRepository extends JpaRepository<ScreeningSeat, Long> {
    List<ScreeningSeat> findByScreeningId(Long screeningId);
    Optional<ScreeningSeat> findByScreeningIdAndSeatId(Long screeningId, Long seatId);
    int countByScreeningIdAndOccupiedTrue(Long screeningId);
    boolean existsByScreeningIdAndSeatIdAndOccupiedTrue(Long screeningId, Long seatId);
    List<ScreeningSeat> findByReservedUntilBefore(LocalDateTime cutoff);
    void deleteByScreeningId(Long screeningId);
}
