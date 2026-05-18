package com.cine.demo.model;

import com.cine.demo.model.enums.SeatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "seat", uniqueConstraints = @UniqueConstraint(columnNames = {"theater_id", "seat_row", "seat_number"}))
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
    @Column(name = "seat_row")
    private String row;

    @Min(1)
    @Column(name = "seat_number")
    private int number;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType type;
}
