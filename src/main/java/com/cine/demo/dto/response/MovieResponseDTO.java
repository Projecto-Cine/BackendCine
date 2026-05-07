package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int durationMin;
    private String genre;
    private String ageRating;
    private String imageUrl;
    private boolean active;
    private String language;
    private String schedule;
    private LocalDateTime createdAt;
}