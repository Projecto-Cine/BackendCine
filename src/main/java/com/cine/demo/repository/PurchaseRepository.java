package com.cine.demo.repository;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    List<Purchase> findByScreeningId(Long screeningId);
    List<Purchase> findByUserIdAndStatus(Long userId, PurchaseStatus status);
    boolean existsByScreeningIdAndTickets_Seat_Id(Long screeningId, Long seatId);
}
