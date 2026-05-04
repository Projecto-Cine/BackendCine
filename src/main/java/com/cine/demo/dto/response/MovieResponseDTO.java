package com.cine.demo.dto.response;

import com.cine.demo.model.enums.AgeRating;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String director;
    private Integer year;
    private String genre;
    private String language;
    private String format;
    private Integer durationMin;
    private AgeRating ageRating;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}
