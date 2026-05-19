package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record MovieResponseDTO(
        Long id,
        String title,
        String description,
        int durationMin,
        String genre,
        String ageRating,
        String posterUrl,
        String imageUrl,
        boolean active,
        String language,
        String schedule,
        LocalDateTime createdAt
) {}
