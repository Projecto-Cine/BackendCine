package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateMovieRequestDTO(
        String title,
        String description,
        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer durationMin,
        String genre,
        AgeRating ageRating,
        String language,
        String schedule,
        String imageUrl
) {}
