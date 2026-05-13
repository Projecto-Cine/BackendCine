package com.cine.demo.repository;

import com.cine.demo.model.MerchandiseSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MerchandiseSaleRepository extends JpaRepository<MerchandiseSale, Long> {
    List<MerchandiseSale> findByPurchaseId(Long purchaseId);

    @Query("SELECT COALESCE(SUM(ms.total), 0) FROM MerchandiseSale ms WHERE YEAR(ms.saleDate) = :year")
    BigDecimal sumRevenueByYear(@Param("year") int year);

    @Query("SELECT ms.merchandise.id, ms.merchandise.name, SUM(ms.total) FROM MerchandiseSale ms WHERE YEAR(ms.saleDate) = :year GROUP BY ms.merchandise.id, ms.merchandise.name ORDER BY SUM(ms.total) DESC")
    List<Object[]> findTopMerchandiseByYear(@Param("year") int year);
}
