package com.cine.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false)
    private String action;

    private String resource;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String ip;

    @Builder.Default
    private String severity = "info";
}