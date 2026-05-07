package com.cine.demo.repository;

import com.cine.demo.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByTitulo(String titulo);
    long countByActiveTrue();
}
