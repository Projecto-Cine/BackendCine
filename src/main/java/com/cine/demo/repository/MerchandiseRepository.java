package com.cine.demo.repository;

import com.cine.demo.model.Merchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long> {
    List<Merchandise> findByActiveTrue();
}
