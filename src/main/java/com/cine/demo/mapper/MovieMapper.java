package com.cine.demo.mapper;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.request.UpdateMovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO dto) {
        return Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMin(dto.getDurationMin())
                .genre(dto.getGenre())
                .ageRating(dto.getAgeRating())
                .language(dto.getLanguage())
                .schedule(dto.getSchedule())
                .build();
    }

    public MovieResponseDTO toResponseDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMin(movie.getDurationMin())
                .genre(movie.getGenre())
                .ageRating(movie.getAgeRating() != null ? movie.getAgeRating().name() : null)
                .imageUrl(movie.getImageUrl())
                .active(movie.isActive())
                .language(movie.getLanguage())
                .schedule(movie.getSchedule())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateMovieRequestDTO dto, Movie movie) {
        if (dto.getTitle() != null) movie.setTitle(dto.getTitle());
        if (dto.getDescription() != null) movie.setDescription(dto.getDescription());
        if (dto.getDurationMin() != null) movie.setDurationMin(dto.getDurationMin());
        if (dto.getGenre() != null) movie.setGenre(dto.getGenre());
        if (dto.getAgeRating() != null) movie.setAgeRating(dto.getAgeRating());
        if (dto.getLanguage() != null) movie.setLanguage(dto.getLanguage());
        if (dto.getSchedule() != null) movie.setSchedule(dto.getSchedule());
    }
}