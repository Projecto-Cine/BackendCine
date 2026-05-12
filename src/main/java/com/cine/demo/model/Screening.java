package com.cine.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screening")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Theater theater;

    @NotNull
    @Column(name = "start_datetime")
    private LocalDateTime startTime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Builder.Default
    @Column(name = "occupied_seats")
    private int occupiedSeats = 0;

    @Builder.Default
    @Column(name = "is_full")
    private boolean full = false;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "base_price")
    private BigDecimal basePrice;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ScreeningSeat> screeningSeats = new ArrayList<>();
}
