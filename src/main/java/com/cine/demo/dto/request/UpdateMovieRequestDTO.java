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

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMin;

    private String genre;
    private AgeRating ageRating;
    private String language;
    private String schedule;
    private String imageUrl;
}
