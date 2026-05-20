package com.cine.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.cine.demo.model.enums.AgeRating;

@Entity
@Table(name = "movie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Min(1)
    @Column(name = "duration_min")
    private int durationMin;

    @NotBlank
    @Column(name = "genre")
    private String genre;

    @Column(name = "age_rating")
    private AgeRating ageRating;

    @Column(name = "image_url")
    private String posterUrl;

    @Builder.Default
    private boolean active = true;

    private String language;

    private String schedule;

    @Column(name = "format", length = 20, nullable = false)
    @Builder.Default
    private String format = "2D";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
