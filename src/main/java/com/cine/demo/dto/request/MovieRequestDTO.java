package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MovieRequestDTO(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @Min(value = 1, message = "Duration must be at least 1 minute")
        int durationMin,
        @NotBlank(message = "Genre is required")
        String genre,
        @NotNull(message = "Age rating is required")
        AgeRating ageRating,
        String language,
        String schedule,
        String imageUrl,
        String format
) {}
