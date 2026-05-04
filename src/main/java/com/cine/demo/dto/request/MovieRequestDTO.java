package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {
    private String title;
    private String description;
    private String genre;
    private Integer durationMin;
    private AgeRating ageRating;
    private String imageUrl;
}
