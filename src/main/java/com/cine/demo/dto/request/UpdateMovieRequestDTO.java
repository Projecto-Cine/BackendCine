package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateMovieRequestDTO(
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer durationMin,

        String genre,
        AgeRating ageRating,
        String language,
        String schedule
) {}
