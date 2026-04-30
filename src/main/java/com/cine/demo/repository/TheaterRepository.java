package com.cine.demo.repository;

import com.cine.demo.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
    boolean existsByNombre(String nombre);
}
