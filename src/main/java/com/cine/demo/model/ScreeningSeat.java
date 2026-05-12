package com.cine.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "screening_seat", uniqueConstraints = @UniqueConstraint(columnNames = {"screening_id", "seat_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Seat seat;

    @Builder.Default
    @Column(name = "occupied")
    private boolean occupied = false;
}
