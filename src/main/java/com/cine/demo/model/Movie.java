package com.cine.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.AgeRatingConverter;

@Entity
@Table(name = "movie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String titulo;

    @Column(name = "description")
    private String descripcion;

    @Min(1)
    @Column(name = "duration_min")
    private int duracionMin;

    @NotBlank
    @Column(name = "genre")
    private String genero;

    @Column(name = "age_rating")
    private AgeRating clasificacionEdad;

    @Column(name = "image_url")
    private String posterUrl;

    @Builder.Default
    private boolean active = true;

    private String language;

    private String schedule;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
