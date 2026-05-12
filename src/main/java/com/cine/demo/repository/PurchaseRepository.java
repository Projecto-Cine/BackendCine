package com.cine.demo.repository;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    List<Purchase> findByScreeningId(Long screeningId);
    Optional<Purchase> findByPaymentIntentId(String paymentIntentId);
    List<Purchase> findByUserIdAndStatus(Long userId, PurchaseStatus status);
    boolean existsByScreeningIdAndTickets_Seat_Id(Long screeningId, Long seatId);
    List<Purchase> findByStatusAndCreatedAtBetween(PurchaseStatus status, LocalDateTime from, LocalDateTime to);
    long countByStatus(PurchaseStatus status);

    @Query("""
            SELECT p FROM Purchase p
            WHERE (:status IS NULL OR p.status = :status)
              AND (:from IS NULL OR p.createdAt >= :from)
              AND (:to IS NULL OR p.createdAt <= :to)
            """)
    List<Purchase> findByStatusAndDateRange(
            @Param("status") PurchaseStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") PurchaseStatus status);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.status = :status AND p.createdAt >= :from")
    BigDecimal sumRevenueSince(@Param("status") PurchaseStatus status, @Param("from") LocalDateTime from);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.status = :status AND YEAR(p.createdAt) = :year")
    BigDecimal sumRevenueByYear(@Param("status") PurchaseStatus status, @Param("year") int year);

    @Query("SELECT p.screening.movie.id, p.screening.movie.title, SUM(p.totalAmount) FROM Purchase p WHERE p.status = :status AND YEAR(p.createdAt) = :year GROUP BY p.screening.movie.id, p.screening.movie.title ORDER BY SUM(p.totalAmount) DESC")
    List<Object[]> findTopMoviesByYear(@Param("status") PurchaseStatus status, @Param("year") int year);
}
