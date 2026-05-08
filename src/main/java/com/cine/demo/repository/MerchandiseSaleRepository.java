package com.cine.demo.repository;

import com.cine.demo.model.MerchandiseSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchandiseSaleRepository extends JpaRepository<MerchandiseSale, Long> {
}
