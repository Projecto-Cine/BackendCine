package com.cine.demo.repository;

import com.cine.demo.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByClientId(Long clientId);

    boolean existsByClientId(Long clientId);

    @Query("SELECT s FROM Socio s WHERE " +
           "LOWER(s.client.name)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.client.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(s.client.phone,'')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.membershipNumber) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Socio> search(@Param("q") String q);

    long count();
}