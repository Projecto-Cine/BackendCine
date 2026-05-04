package com.cine.demo.model;

import com.cine.demo.model.enums.ScreeningStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screenings")
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
    private LocalDateTime dateTime;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal basePrice;

    private int availableSeats;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreeningStatus status = ScreeningStatus.SCHEDULED;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ScreeningSeat> screeningSeats = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}