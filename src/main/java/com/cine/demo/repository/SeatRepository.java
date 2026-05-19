package com.cine.demo.repository;

import com.cine.demo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheaterId(Long theaterId);
    boolean existsByTheaterIdAndRowAndNumber(Long theaterId, String row, int number);
    void deleteByTheaterId(Long theaterId);
}
