package com.cine.demo.repository;

import com.cine.demo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPurchaseId(Long purchaseId);
    List<Ticket> findByScreeningId(Long screeningId);
    boolean existsByScreeningIdAndSeatId(Long screeningId, Long seatId);
    void deleteByScreeningId(Long screeningId);
}
