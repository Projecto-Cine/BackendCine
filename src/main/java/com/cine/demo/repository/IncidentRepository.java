package com.cine.demo.repository;

import com.cine.demo.model.Incident;
import com.cine.demo.model.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    long countByStatusNot(IncidentStatus status);
}
