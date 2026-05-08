package com.cine.demo.model;

import com.cine.demo.model.enums.TicketType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ticket", uniqueConstraints = @UniqueConstraint(columnNames = {"screening_id", "seat_id"}))
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
    @Column(name = "ticket_type")
    @NotNull
    private TicketType ticketType;

    @NotNull
    @Column(name = "price")
    private BigDecimal unitPrice;
}
