package com.cine.demo.repository;

import com.cine.demo.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findBySeverityOrderByTimestampDesc(String severity);

    List<AuditLog> findAllByOrderByTimestampDesc();

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:severity IS NULL OR a.severity = :severity) AND " +
           "(:user IS NULL OR a.user LIKE %:user%) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:from IS NULL OR a.timestamp >= :from) AND " +
           "(:to IS NULL OR a.timestamp <= :to) " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> findWithFilters(
            @Param("severity") String severity,
            @Param("user") String user,
            @Param("action") String action,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}