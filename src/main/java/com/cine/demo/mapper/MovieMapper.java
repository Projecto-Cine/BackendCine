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
                .title(dto.title())
                .description(dto.description())
                .durationMin(dto.durationMin())
                .genre(dto.genre())
                .ageRating(dto.ageRating())
                .language(dto.language())
                .schedule(dto.schedule())
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
                .posterUrl(movie.getPosterUrl())
                .imageUrl(movie.getPosterUrl())
                .active(movie.isActive())
                .language(movie.getLanguage())
                .schedule(movie.getSchedule())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateMovieRequestDTO dto, Movie movie) {
        if (dto.title() != null) movie.setTitle(dto.title());
        if (dto.description() != null) movie.setDescription(dto.description());
        if (dto.durationMin() != null) movie.setDurationMin(dto.durationMin());
        if (dto.genre() != null) movie.setGenre(dto.genre());
        if (dto.ageRating() != null) movie.setAgeRating(dto.ageRating());
        if (dto.language() != null) movie.setLanguage(dto.language());
        if (dto.schedule() != null) movie.setSchedule(dto.schedule());
    }
}
