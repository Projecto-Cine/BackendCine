package com.cine.demo.repository;

import com.cine.demo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheaterId(Long theaterId);
    boolean existsByTheaterIdAndFilaAndNumero(Long theaterId, String fila, int numero);
}
