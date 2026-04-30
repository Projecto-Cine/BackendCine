package com.cine.demo.model;

import com.cine.demo.model.enums.TicketType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", uniqueConstraints = @UniqueConstraint(columnNames = {"screening_id", "seat_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screening screening;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TicketType ticketType;

    @NotNull
    private BigDecimal unitPrice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
