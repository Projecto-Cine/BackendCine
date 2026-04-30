package com.cine.demo.repository;

import com.cine.demo.model.ScreeningSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningSeatRepository extends JpaRepository<ScreeningSeat, Long> {
    List<ScreeningSeat> findByScreeningId(Long screeningId);
    Optional<ScreeningSeat> findByScreeningIdAndSeatId(Long screeningId, Long seatId);
    int countByScreeningIdAndOcupadoTrue(Long screeningId);
    boolean existsByScreeningIdAndSeatIdAndOcupadoTrue(Long screeningId, Long seatId);
}
