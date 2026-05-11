package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMin;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Age rating is required")
    private AgeRating ageRating;

    private String language;

    private String schedule;
}
