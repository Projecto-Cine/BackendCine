package com.cine.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.AgeRatingConverter;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String director;

    private Integer year;

    private String genre;

    private String language;

    private String format;

    @Column(name = "duration_min", nullable = false)
    private Integer durationMin;

    @Convert(converter = AgeRatingConverter.class)
    private AgeRating ageRating;

    private String imageUrl;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
