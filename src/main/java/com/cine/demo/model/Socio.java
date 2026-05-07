package com.cine.demo.model;

import com.cine.demo.model.enums.MembershipStatus;
import com.cine.demo.model.enums.MembershipType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "socios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(unique = true, nullable = false)
    private String membershipNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipType membershipType = MembershipType.BASIC;

    @Builder.Default
    private int discountPct = 10;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.active;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    private LocalDate expiresAt;

    @Column(length = 1000)
    private String notes;
}