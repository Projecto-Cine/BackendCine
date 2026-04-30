package com.cine.demo.model;

import com.cine.demo.model.enums.AgeRating;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    private String descripcion;

    @Min(1)
    private int duracionMin;

    @NotBlank
    private String genero;

    @NotBlank
    private String clasificacionEdad;

    private String posterUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AgeRating ageRating = AgeRating.ALL;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
