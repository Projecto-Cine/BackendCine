package com.cine.demo.mapper;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO dto) {
        return Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .durationMin(dto.getDurationMin())
                .ageRating(dto.getAgeRating())
                .imageUrl(dto.getImageUrl())
                .active(true)
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    public MovieResponseDTO toResponseDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .durationMin(movie.getDurationMin())
                .ageRating(movie.getAgeRating())
                .imageUrl(movie.getImageUrl())
                .active(movie.getActive())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(MovieRequestDTO dto, Movie movie) {
        if (dto.getTitle() != null) movie.setTitle(dto.getTitle());
        if (dto.getDescription() != null) movie.setDescription(dto.getDescription());
        if (dto.getGenre() != null) movie.setGenre(dto.getGenre());
        if (dto.getDurationMin() != null) movie.setDurationMin(dto.getDurationMin());
        if (dto.getAgeRating() != null) movie.setAgeRating(dto.getAgeRating());
        if (dto.getImageUrl() != null) movie.setImageUrl(dto.getImageUrl());
    }
}
