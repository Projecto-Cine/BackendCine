package com.cine.demo.model;

import com.cine.demo.model.enums.SeatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"theater_id", "fila", "numero"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Theater theater;

    @NotBlank
    private String fila;

    @Min(1)
    private int numero;

    @Enumerated(EnumType.STRING)
    private SeatType tipo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
