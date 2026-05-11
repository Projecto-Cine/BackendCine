package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequestDTO {
    private String title;
    private String description;

    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private Integer durationMin;

    private String genre;
    private AgeRating ageRating;
    private String language;
    private String schedule;
}
